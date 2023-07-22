package discord.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import discord.enums.GatewayEvent;
import discord.enums.GatewayIntent;
import discord.enums.GatewayOpcode;
import discord.resources.AuditLogEntry;
import discord.resources.channels.MessageChannel;
import discord.resources.interactions.Interaction;
import discord.util.Util;
import sj.Sj;

public class GatewayClient extends WebSocketClient {
	private static final URI DISCORD_GATEWAY_URI;

	static {
		try {
			DISCORD_GATEWAY_URI = new URI("wss://gateway.discord.gg/");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

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

	public void login(GatewayIntent... intents) {
		try { if (!connectBlocking()) System.exit(1); }
		catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		send("""
			{
				"op": %d,
				"d": {
					"token": "%s",
					"intents": %d,
					"properties": {
						"os": "%s",
						"browser": "discordjs4j",
						"device": "discordjs4j"
					}
				}
			}
			""".formatted(
				GatewayOpcode.IDENTIFY.value,
				token,
				GatewayIntent.sum(intents),
				System.getProperty("os.name")
			)
		);
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

	public void onMessageAsync(String rawJson) {
		final var obj = Sj.parseObject(rawJson);
		final var opcode = GatewayOpcode.resolve(obj.getShort("op"));

		switch (opcode) {
			case DISPATCH -> {
				sequenceNumber = obj.getLong("s");

				final var t = obj.getString("t");
				System.out.printf("[GatewayClient] Event received: %s\n", t);

				final var event = GatewayEvent.valueOf(t);
				if (event == null) return;

				switch (event) {
					case READY -> {
						final var d = obj.getObject("d");
						System.out.println(d.getObjectArray("guilds").stream().map(o -> o.toPrettyJsonString()).toList());
						client.users.cache(client.user);
						client.ready.emit();
					}

					case INTERACTION_CREATE -> {
						final var bot = (BotDiscordClient) client;
						bot.interactionCreate.emit(Interaction.fromJSON(bot, obj.getObject("d")));
					}

					case GUILD_AUDIT_LOG_ENTRY_CREATE -> client.auditLogEntryCreate.emit(new AuditLogEntry(client, obj.getObject("d")));

					case GUILD_CREATE -> client.guildCreate.emit(client.guilds.cache(obj.getObject("d")));
					case GUILD_UPDATE -> client.guildUpdate.emit(client.guilds.cache(obj.getObject("d")));
					case GUILD_DELETE -> {
						final var id = obj.getObject("d").getString("id");
						final var removed = client.guilds.cache.remove(id);
						if (removed == null) return;
						client.guildDelete.emit(removed);
					}

					case CHANNEL_CREATE -> client.channelCreate.emit(client.channels.cache(obj.getObject("d")));
					case CHANNEL_UPDATE -> client.channelUpdate.emit(client.channels.cache(obj.getObject("d")));
					case CHANNEL_DELETE -> {
						final var id = obj.getObject("d").getString("id");
						final var removed = client.channels.cache.remove(id);
						if (removed == null) return;
						client.channelDelete.emit(removed);
					}

					case MESSAGE_CREATE -> {
						final var messageObj = obj.getObject("d");
						client.channels.get(messageObj.getString("channel_id"))
							.thenAccept(c -> {
								final var channel = (MessageChannel) c;
								final var message = channel.messages().cache(messageObj);
								client.messageCreate.emit(message);
							});
					}

					case MESSAGE_UPDATE -> {
						final var messageObj = obj.getObject("d");
						client.channels.get(messageObj.getString("channel_id"))
							.thenAccept(c -> {
								final var channel = (MessageChannel) c;
								final var message = channel.messages().cache(messageObj);
								client.messageUpdate.emit(message);
							});
					}

					case MESSAGE_DELETE -> {
						final var d = obj.getObject("d");
						client.channels.get(d.getString("channel_id"))
							.thenAccept(c -> {
								final var channel = (MessageChannel) c;
								final var message = channel.messages().cache.remove(d.getString("id"));
								client.messageDelete.emit(message);
							});
					}

					default -> {
						final var dataString = obj.getObject("d").toString();
						if (dataString.length() < 1000) System.out.println(dataString);
					}
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
}
