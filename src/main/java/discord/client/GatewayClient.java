package discord.client;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import discord.enums.GatewayEvent;
import discord.enums.GatewayIntent;
import discord.enums.GatewayOpcode;
import discord.resources.channels.MessageChannel;
import discord.structures.AuditLogEntry;
import discord.structures.IdentifyParams;
import discord.structures.interactions.Interaction;
import discord.util.Util;
import sj.Sj;
import sj.SjObject;

public class GatewayClient extends WebSocketClient {
	private static void debugPrint(String message) {
		System.out.println("[GatewayClient] " + message);
	}

	private static final URI DISCORD_GATEWAY_URI = URI.create("wss://gateway.discord.gg");

	private final Timer heartbeatTimer = new Timer();
	private final DiscordClient client;
	private final boolean debug;
	private final String token;
	private long sequenceNumber, heartbeatSentAt, ping;

	public GatewayClient(DiscordClient client, String token, boolean debug) {
		super(DISCORD_GATEWAY_URI);
		this.client = client;
		this.token = token;
		this.debug = debug;
	}

	public long getPing() {
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
				// also a good idea to print this regardless of this.debug
				debugPrint("connectBlocking() failed!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendIdentify(String payload) {
		if (debug)
			debugPrint("Sending identify payload: " + payload);
		send(payload);
	}

	private void sendIdentify(GatewayIntent... intents) {
		final var d = Map.of(
				"token", token,
				"properties", IdentifyParams.DEFAULT_CONNECTION_PROPERTIES,
				"intents", GatewayIntent.sum(intents));
		sendIdentify("{\"op\":2,\"d\":" + Sj.write(d) + "}");
	}

	private void sendIdentify(IdentifyParams params) {
		sendIdentify("{\"op\":2,\"d\":" + params.toJsonString() + "}");
	}

	public void sendRequestGuildMembers(String guildId, String query, int limit, boolean presences,
			List<String> userIds, String nonce) {
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
		final var payload = "{\"op\":8,\"d\":" + obj.toJsonString() + "}";
		if (debug)
			debugPrint("Sending Request Guild Members payload: " + payload);
		send(payload);
	}

	@Override
	public void onOpen(ServerHandshake handshake) {
		if (debug)
			debugPrint("""
					Connection to Discord gateway opened
						Status code: %d
						Status message: %s
						Content: %s
					""".formatted(
					handshake.getHttpStatus(),
					handshake.getHttpStatusMessage(),
					handshake.getContent()));
	}

	@Override
	public void onMessage(String message) {
		CompletableFuture.runAsync(() -> onMessageAsync(message)).exceptionally(Util::printStackTrace);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		if (debug)
			debugPrint("""
					Connection closed!
						Code: %d
						Reason: %s
						Remote: %b
					""".formatted(code, reason, remote));
	}

	@Override
	public void onError(Exception e) {
		// this prints regardless of this.debug since it's
		// probably a good idea to show users of this class
		// any exceptions that occur
		debugPrint("WebSocket error occurred! Details below:");
		e.printStackTrace();
	}

	private void onMessageAsync(String rawJson) {
		final var obj = Sj.parseObject(rawJson);
		final var opcode = GatewayOpcode.resolve(obj.getShort("op"));

		switch (opcode) {
			case DISPATCH -> {
				sequenceNumber = obj.getLong("s");

				final var t = obj.getString("t");
				if (debug)
					debugPrint("Event received: " + t);

				switch (GatewayEvent.valueOf(t)) {
					case READY -> {
						// this is really only useful for users
						// TODO: get more initial data from the ready event for USER clients
						// final var d = obj.getObject("d");
						client.onReady();
					}

					case INTERACTION_CREATE -> {
						final var bot = (BotDiscordClient) client;
						bot.onInteractionCreate(Interaction.construct(bot, obj.getObject("d")));
					}

					case GUILD_AUDIT_LOG_ENTRY_CREATE ->
						client.onGuildAuditLogEntryCreate(new AuditLogEntry(client, obj.getObject("d")));

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
						System.out.println(id);
						final var deletedChannel = client.channels.cache.get(id);
						deletedChannel.setDeleted();
						client.onChannelDelete(deletedChannel);
					}

					case MESSAGE_CREATE -> {
						final var messageObj = obj.getObject("d");
						client.channels.get(messageObj.getString("channel_id"))
							.thenAccept(c -> {
								final var channel = (MessageChannel) c;
								final var message = channel.getMessageManager().cache(messageObj);
								client.onMessageCreate(message);
							});
					}

					case MESSAGE_UPDATE -> {
						final var messageObj = obj.getObject("d");
						client.channels.get(messageObj.getString("channel_id"))
							.thenAccept(c -> {
								final var channel = (MessageChannel) c;
								final var message = channel.getMessageManager().cache(messageObj);
								client.onMessageUpdate(message);
							});
					}

					case MESSAGE_DELETE -> {
						final var d = obj.getObject("d");
						client.channels.get(d.getString("channel_id"))
							.thenAccept(c -> {
								final var channel = (MessageChannel) c;
								final var deletedMessage = channel.getMessageManager().cache.get(d.getString("id"));
								deletedMessage.setDeleted();
								client.onMessageDelete(deletedMessage);
							});
					}

					default -> {
					}
				}
			}

			case HEARTBEAT_ACK -> {
				ping = System.currentTimeMillis() - heartbeatSentAt;
				if (debug)
					debugPrint("Heartbeat ACK received; Ping: " + ping + "ms");
			}

			case HELLO -> {
				final var d = obj.getObject("d");

				// Interval in milliseconds that Discord wants us to wait before
				// sending another heartbeat.
				final var heartbeatInterval = d.getLong("heartbeat_interval");
				if (debug)
					debugPrint("Hello event received; Heartbeat interval: " + heartbeatInterval + "ms");

				heartbeatTimer.schedule(new TimerTask() {
					public void run() {
						if (debug)
							debugPrint("Sending heartbeat; Sequence number: " + sequenceNumber);
						send("{\"op\":" + GatewayOpcode.HEARTBEAT.value + ",\"d\":" + sequenceNumber + "}");
						heartbeatSentAt = System.currentTimeMillis();
					}
				}, 0, heartbeatInterval);
			}

			default -> {
			}
		}
	}
}
