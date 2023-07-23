package discord.client;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import discord.enums.GatewayEvent;
import discord.enums.GatewayIntent;
import discord.enums.GatewayOpcode;
import discord.resources.channels.MessageChannel;
import discord.resources.interactions.Interaction;
import discord.structures.AuditLogEntry;
import discord.structures.IdentifyParams;
import discord.util.Util;
import sj.Sj;
import sj.SjObject;

public class GatewayClient extends WebSocketClient {
	private static final URI DISCORD_GATEWAY_URI = URI.create("wss://gateway.discord.gg");

	private final DiscordClient client;
	private final String token;
	private long sequenceNumber, heartbeatSentAt, ping;

	public GatewayClient(DiscordClient client, String token) {
		super(DISCORD_GATEWAY_URI);
		this.client = client;
		this.token = token;
	}

	public long ping() {
		return ping;
	}



	public void connectAndIdentify(GatewayIntent... intents) {
		tryConnecting();
		sendIdentify(intents);
	}

	public void connectAndIdentify(IdentifyParams params) {
		tryConnecting();
		sendIdentify(params);
	}

	private void tryConnecting() {
		try {
			if (!connectBlocking())
				throw new RuntimeException("Could not connect websocket to Discord gateway!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendIdentify(GatewayIntent... intents) {
		final var d = Map.of(
			"token", token,
			"properties", IdentifyParams.DEFAULT_CONNECTION_PROPERTIES,
			"intents", GatewayIntent.sum(intents)
		);
		send("{\"op\":2,\"d\":%s}".formatted(Sj.write(d)));
	}

	private void sendIdentify(IdentifyParams params) {
		send("{\"op\":2,\"d\":%s}".formatted(params.toJsonString()));
	}

	public void sendRequestGuildMembers(String guildId, String query, int limit, boolean presences, List<String> userIds, String nonce) {
		final var obj = new SjObject();
		obj.put("guild_id", Objects.requireNonNull(guildId));
		if (query != null) {
			obj.put("query", query);
			obj.put("limit", limit);
		}
		if (presences)
			obj.put("presences", Boolean.TRUE);
		if (userIds != null && userIds.size() > 0)
			obj.put("user_ids", userIds);
		if (nonce != null)
			obj.put("nonce", nonce);
		send("""
				{
					"op": 8,
					"d": %s
				}
				""".formatted(obj.toJsonString()));
	}

	@Override
	public void onOpen(ServerHandshake handshake) {
		System.out.printf("""
			[GatewayClient] Connection to Discord gateway opened
				Status code: %d
				Status message: %s
				Content: %s
			""",
			handshake.getHttpStatus(),
			handshake.getHttpStatusMessage(),
			handshake.getContent());
	}

	@Override
	public void onMessage(String message) {
		CompletableFuture.runAsync(() -> onMessageAsync(message)).exceptionally(Util::printStackTrace);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.printf("""
				[GatewayClient] Connection closed!
					Code: %d
					Reason: %s
					Remote: %b
				""", code, reason, remote);
		System.exit(1);
	}

	@Override
	public void onError(Exception e) {
		System.err.println("[GatewayClient] WebSocket error!");
		e.printStackTrace();
	}

	private void onMessageAsync(String rawJson) {
		final var obj = Sj.parseObject(rawJson);
		final var opcode = GatewayOpcode.resolve(obj.getShort("op"));

		switch (opcode) {
			case DISPATCH -> {
				sequenceNumber = obj.getLong("s");

				final var t = obj.getString("t");
				System.out.printf("[GatewayClient] Event received: %s\n", t);

				switch (GatewayEvent.valueOf(t)) {
					case READY -> {
						// this is really only useful for users
						// TODO: get more initial data from the ready event for USER clients
						//final var d = obj.getObject("d");
						client.onReady();
					}

					case INTERACTION_CREATE -> {
						final var bot = (BotDiscordClient) client;
						bot.onInteractionCreate(Interaction.construct(bot, obj.getObject("d")));
					}

					case GUILD_AUDIT_LOG_ENTRY_CREATE -> client.onGuildAuditLogEntryCreate(new AuditLogEntry(client, obj.getObject("d")));

					case GUILD_CREATE -> client.onGuildCreate(client.guilds.cache(obj.getObject("d")));
					case GUILD_UPDATE -> client.onGuildUpdate(client.guilds.cache(obj.getObject("d")));
					case GUILD_DELETE -> {
						final var id = obj.getObject("d").getString("id");
						final var deletedGuild = client.guilds.cache.get(id);
						deletedGuild.setDeleted();
						client.onGuildDelete(deletedGuild);
					}

					case CHANNEL_CREATE -> client.onChannelCreate(client.channels.cache(obj.getObject("d")));
					case CHANNEL_UPDATE -> client.onChannelUpdate(client.channels.cache(obj.getObject("d")));
					case CHANNEL_DELETE -> {
						final var id = obj.getObject("d").getString("id");
						final var deletedChannel = client.channels.cache.get(id);
						deletedChannel.setDeleted();
						client.onChannelDelete(deletedChannel);
					}

					case MESSAGE_CREATE -> {
						final var messageObj = obj.getObject("d");
						client.channels.get(messageObj.getString("channel_id"))
							.thenAccept(c -> {
								final var channel = (MessageChannel) c;
								final var message = channel.messages().cache(messageObj);
								client.onMessageCreate(message);
							});
					}

					case MESSAGE_UPDATE -> {
						final var messageObj = obj.getObject("d");
						client.channels.get(messageObj.getString("channel_id"))
							.thenAccept(c -> {
								final var channel = (MessageChannel) c;
								final var message = channel.messages().cache(messageObj);
								client.onMessageUpdate(message);
							});
					}

					case MESSAGE_DELETE -> {
						final var d = obj.getObject("d");
						client.channels.get(d.getString("channel_id"))
							.thenAccept(c -> {
								final var channel = (MessageChannel) c;
								final var deletedMessage = channel.messages().cache.get(d.getString("id"));
								deletedMessage.setDeleted();
								client.onMessageDelete(deletedMessage);
							});
					}

					default -> {}
				}
			}

			case HEARTBEAT_ACK -> {
				ping = System.currentTimeMillis() - heartbeatSentAt;
				System.out.printf("[GatewayClient] Heartbeat ACK received; Ping: %sms\n", ping);
			}

			case HELLO -> {
				final var d = obj.getObject("d");

				// Interval in milliseconds that Discord wants us to wait before
				// sending another heartbeat.
				final var heartbeat_interval = d.getLong("heartbeat_interval");
				System.out.printf("[GatewayClient] Hello event received; Heartbeat interval: %dms\n", heartbeat_interval);

				Util.repeat(() -> {
					System.out.printf("[GatewayClient] Sending heartbeat; Sequence number: %d\n", sequenceNumber);
					send("""
							{
								"op": %d,
								"d": %d
							}
						""".formatted(GatewayOpcode.HEARTBEAT.value, sequenceNumber));
					heartbeatSentAt = System.currentTimeMillis();
				}, heartbeat_interval);
			}

			default -> {}
		}
	}
}
