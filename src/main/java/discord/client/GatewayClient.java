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
import discord.util.Logger;
import discord.util.Util;
import sj.Sj;
import sj.SjObject;

public class GatewayClient extends WebSocketClient {
	private static final URI DISCORD_GATEWAY_URI = URI.create("wss://gateway.discord.gg");

	private final Timer heartbeatTimer = new Timer();
	private final DiscordClient client;
	private final boolean debug;
	private final String token;
	private long sequenceNumber, heartbeatSentAt, ping;

	public GatewayClient(final DiscordClient client, final String token, final boolean debug) {
		super(DISCORD_GATEWAY_URI);
		this.client = client;
		this.token = token;
		this.debug = debug;
		Runtime.getRuntime().addShutdownHook(new Thread(this::close));
	}

	public long getPing() {
		return ping;
	}

	public void connectAndIdentify(final GatewayIntent... intents) {
		tryConnecting();
		sendIdentify(intents);
	}

	public void connectAndIdentify(final IdentifyParams params) {
		tryConnecting();
		sendIdentify(params);
	}

	private void tryConnecting() {
		try {
			if (!connectBlocking())
				// also a good idea to print this regardless of this.debug
				Logger.log("connectBlocking() failed!");
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendIdentify(final String payload) {
		if (debug)
			Logger.log("Sending identify payload: " + payload);
		send(payload);
	}

	private void sendIdentify(final GatewayIntent... intents) {
		sendIdentify("{\"op\":2,\"d\":" + Sj.write(Map.of(
			"token", token,
			"properties", IdentifyParams.DEFAULT_CONNECTION_PROPERTIES,
			"intents", GatewayIntent.sum(intents)
		)) + "}");
	}

	private void sendIdentify(final IdentifyParams params) {
		sendIdentify("{\"op\":2,\"d\":" + params.toJsonString() + "}");
	}

	public void requestGuildMembers(
		final String guildId,
		final String query,
		final int limit,
		final boolean presences,
		final List<String> userIds,
		final String nonce
	) {
		final var obj = new SjObject();
		obj.put("guild_id", Objects.requireNonNull(guildId));
		if (query != null) {
			obj.put("query", query);
			obj.put("limit", limit);
		}
		if (presences)
			obj.put("presences", true);
		if (userIds != null && userIds.size() > 0)
			obj.put("user_ids", userIds);
		if (nonce != null)
			obj.put("nonce", nonce);
		final var payload = "{\"op\":8,\"d\":" + obj.toJsonString() + "}";
		if (debug)
			Logger.log("Sending Request Guild Members payload: " + payload);
		send(payload);
	}

	@Override
	public void onOpen(final ServerHandshake handshake) {
		if (debug)
			Logger.log("""
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
	public void onMessage(final String data) {
		CompletableFuture.runAsync(() -> onMessageAsync(data)).exceptionally(Util::printStackTrace);
	}

	@Override
	public void onClose(final int code, final String reason, final boolean remote) {
		if (debug)
			Logger.log("""
				Connection closed!
					Code: %d
					Reason: %s
					Remote: %b
				""".formatted(code, reason, remote));
	}

	@Override
	public void onError(final Exception e) {
		if (debug)
			Logger.log("WebSocket error occurred!");
		e.printStackTrace();
	}

	private void onMessageAsync(final String data) {
		final var obj = Sj.parseObject(data);
		final var opcode = GatewayOpcode.resolve(obj.getShort("op"));

		switch (opcode) {
			case DISPATCH -> {
				sequenceNumber = obj.getLong("s");

				final var t = obj.getString("t");
				if (debug)
					Logger.log("Event received: " + t);

				final GatewayEvent ev;
				try { ev = GatewayEvent.valueOf(t); }
				catch (final IllegalArgumentException e) { return; }

				switch (ev) {
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
						deletedGuild.markAsDeleted();
						client.onGuildDelete(deletedGuild);
					}

					case CHANNEL_CREATE -> client.onChannelCreate(client.channels.cache(obj.getObject("d")));
					case CHANNEL_UPDATE -> client.onChannelUpdate(client.channels.cache(obj.getObject("d")));
					case CHANNEL_DELETE -> {
						final var id = obj.getObject("d").getString("id");
						final var deletedChannel = client.channels.cache.get(id);
						deletedChannel.markAsDeleted();
						client.onChannelDelete(deletedChannel);
					}

					case MESSAGE_CREATE -> {
						final var d = obj.getObject("d");
						client.channels.get(d.getString("channel_id")).thenAccept(c -> {
							final var message = ((MessageChannel) c).getMessageManager().cache(d);
							client.onMessageCreate(message);
						});
					}

					case MESSAGE_UPDATE -> {
						final var d = obj.getObject("d");
						client.channels.get(d.getString("channel_id")).thenAccept(c -> {
							final var message = ((MessageChannel) c).getMessageManager().cache(d);
							client.onMessageUpdate(message);
						});
					}

					case MESSAGE_DELETE -> {
						final var d = obj.getObject("d");
						client.channels.get(d.getString("channel_id")).thenAccept(c -> {
							final var deletedMessage = ((MessageChannel) c).getMessageManager().cache.get(d.getString("id"));
							deletedMessage.markAsDeleted();
							client.onMessageDelete(deletedMessage);
						});
					}

					default -> {}
				}
			}

			case HEARTBEAT_ACK -> {
				ping = System.currentTimeMillis() - heartbeatSentAt;
				if (debug)
					Logger.log("Heartbeat ACK received; Ping: " + ping + "ms");
			}

			// we should only receive this once
			case HELLO -> {
				final var d = obj.getObject("d");

				// interval in milliseconds that discord wants us to wait before sending another heartbeat
				final var heartbeatInterval = d.getLong("heartbeat_interval");
				if (debug)
					Logger.log("Hello event received; Heartbeat interval: " + heartbeatInterval + "ms");

				heartbeatTimer.schedule(new TimerTask() {
					public void run() {
						if (debug)
							Logger.log("Sending heartbeat; Sequence number: " + sequenceNumber);
						send("{\"op\":" + GatewayOpcode.HEARTBEAT.value + ",\"d\":" + sequenceNumber + "}");
						heartbeatSentAt = System.currentTimeMillis();
					}
				}, 0, heartbeatInterval);
			}

			default -> {}
		}
	}
}
