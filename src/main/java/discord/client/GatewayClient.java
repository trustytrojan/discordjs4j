package discord.client;

import java.net.ConnectException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import discord.enums.GatewayEvent;
import discord.enums.GatewayIntent;
import discord.enums.GatewayOpcode;
import discord.resources.channels.MessageChannel;
import discord.structures.Activity;
import discord.structures.AuditLogEntry;
import discord.structures.ClientStatus;
import discord.structures.UpdatePresence;
import discord.structures.interactions.Interaction;
import discord.util.Logger;
import sj.Sj;
import sj.SjObject;

public final class GatewayClient extends WebSocketClient {
	private final Timer heartbeatTimer = new Timer();
	private final DiscordClient client;
	private boolean debug;
	private final String token;
	
	/**
	 * Used for resuming connections
	 */
	private String sessionId;
	
	private long sequenceNumber, heartbeatSentAt, ping;

	GatewayClient(final DiscordClient client, final String token, final boolean debug) {
		super(URI.create("wss://gateway.discord.gg"));
		this.client = client;
		this.token = token;
		this.debug = debug;
		// Runtime.getRuntime().addShutdownHook(new Thread(this::close));
	}

	public long getPing() {
		return ping;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void tryConnect() throws InterruptedException, ConnectException {
		if (!connectBlocking())
			throw new ConnectException("connectBlocking() failed!");
		if (debug)
			Logger.log("Connected to Discord Gateway");
	}

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#identify
	 * @param compress Whether this connection supports compression of packets. Can be {@code null}.
	 * @param largeThreshold Value between 50 and 250, total number of members where the gateway will stop sending offline members in the guild member list. Can be {@code null}.
	 * @param shardId Used for <a href="https://discord.com/developers/docs/topics/gateway#sharding">Guild Sharding</a>.
	 *                Can be {@code null}. Must be sent with {@code numShards}.
	 * @param numShards Used for <a href="https://discord.com/developers/docs/topics/gateway#sharding">Guild Sharding</a>.
	 *                  Can be {@code null}. Must be sent with {@code shardId}.
	 * @param presence Presence structure for initial presence information. Can be {@code null}.
	 * @param intents <a href="https://discord.com/developers/docs/topics/gateway#gateway-intents">Gateway Intents</a> you wish to receive
	 */
	public void identify(
		final boolean compress,
		final Integer largeThreshold,
		final Integer shardId,
		final Integer numShards,
		final UpdatePresence presence,
		final GatewayIntent[] intents
	) {
		final var obj = new SjObject();
		obj.put("properties", Map.of(
			"os", System.getProperty("os.name"),
			"browser", "discordjs4j",
			"device", "discordjs4j"
		));
		if (compress)
			obj.put("compress", true);
		if (largeThreshold != null)
			obj.put("large_threshold", largeThreshold);
		if (shardId != null && numShards != null)
			obj.put("shard", List.of(shardId, numShards));
		if (presence != null)
			obj.put("presence", presence);
		obj.put("intents", GatewayIntent.sum(intents));
		if (debug)
			Logger.log("Sending identify payload (token removed):\n" + obj.toPrettyJsonString());
		obj.put("token", token);
		send("""
			{
				"op": %d,
				"d": %s
			}
			""".formatted(GatewayOpcode.IDENTIFY.value, obj.toPrettyJsonString()));
	}

	public void identify(final GatewayIntent... intents) {
		identify(false, null, null, null, null, intents);
	}

	public void login(
		final boolean compress,
		final Integer largeThreshold,
		final Integer shardId,
		final Integer numShards,
		final UpdatePresence presence,
		final GatewayIntent[] intents
	) throws ConnectException, InterruptedException {
		tryConnect();
		identify(compress, largeThreshold, shardId, numShards, presence, intents);
	}

	public void login(final GatewayIntent... intents) throws ConnectException, InterruptedException {
		tryConnect();
		identify(intents);
	}

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#resume
	 */
	public void resume() {
		if (debug)
			Logger.log("Sending resume payload...");
		send("""
			{
				"op": %d,
				"d": {
					"token": "%s",
					"session_id": "%s",
					"seq": %d
				}
			}
			""".formatted(GatewayOpcode.RESUME.value, token, sessionId, sequenceNumber));
	}

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#request-guild-members
	 * @param guildId ID of the guild to get members for
	 * @param query String that username starts with, or an empty string to return all members
	 * @param limit Maximum number of members to send matching the query; a limit of 0 can be used
	 *              with an empty string query to return all members. Required when specifying {@code query}.
	 * @param presences Used to specify if we want the presences of the matched members
	 * @param userIds Used to specify which users you wish to fetch. Can be {@code null}.
	 * @param nonce Nonce to identify the <a href="https://discord.com/developers/docs/topics/gateway-events#guild-members-chunk">
	 *              Guild Members Chunk</a> response
	 */
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
		final var payload = """
			{
				"op": %d,
				"d": %s
			}	
			""".formatted(GatewayOpcode.REQUEST_GUILD_MEMBERS.value, obj.toPrettyJsonString());
		if (debug)
			Logger.log("Sending Request Guild Members payload: " + payload);
		send(payload);
	}

	public void updatePresence(final UpdatePresence data) {
		send(data.toJsonString());
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

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#payload-structure
	 * <p>
	 * Catch exceptions here to prevent library errors from being treated as WebSocket errors.
	 */
	@Override
	public void onMessage(final String json) {
		try {
			handleData(Sj.parseObject(json));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void handleData(final SjObject obj) {
		switch (GatewayOpcode.resolve(obj.getShort("op"))) {

			// https://discord.com/developers/docs/topics/gateway-events#hello
			case HELLO -> {
				final var heartbeatIntervalMillis = obj.getObject("d").getLong("heartbeat_interval");
				if (debug)
					Logger.log("Hello event received; Heartbeat interval: " + heartbeatIntervalMillis + "ms");
				heartbeatTimer.schedule(new TimerTask() {
					public void run() {
						if (debug)
							Logger.log("Sending heartbeat; Sequence number: " + sequenceNumber);
						send("{\"op\":" + GatewayOpcode.HEARTBEAT.value + ",\"d\":" + sequenceNumber + "}");
						heartbeatSentAt = System.currentTimeMillis();
					}
				}, 0, heartbeatIntervalMillis);
			}

			// https://discord.com/developers/docs/topics/gateway#sending-heartbeats
			case HEARTBEAT_ACK -> {
				ping = System.currentTimeMillis() - heartbeatSentAt;
				if (debug)
					Logger.log("Heartbeat ACK received; Ping: " + ping + "ms");
			}

			case DISPATCH -> { // discord has sent us event data
				sequenceNumber = obj.getLong("s");

				final var t = obj.getString("t");
				if (debug)
					Logger.log("Event received: " + t);

				final GatewayEvent ev;
				try { ev = GatewayEvent.valueOf(t); }
				catch (final IllegalArgumentException e) {
					/**
					 * as long as discord keeps updating, new events will arise,
					 * and i can't keep up with that. so we need to ignore unknown events.
					 */
					return;
				}

				final var d = obj.getObject("d");

				switch (ev) {
					// https://discord.com/developers/docs/topics/gateway-events#ready
					case READY -> {
						sessionId = d.getString("session_id");
						client.onReady();
					}

					// https://discord.com/developers/docs/topics/gateway-events#presence-update
					case PRESENCE_UPDATE -> {
						final var receivedUser = d.getObject("user");
						final var user = client.users.get(receivedUser.getString("id")).join();
						user.overlay(receivedUser);
						client.onPresenceUpdate(user, d.getString("guild_id"), d.getString("status"),
							d.getObjectArray("activities").stream().map(Activity::new).toList(),
							new ClientStatus(d.getObject("client_status")));
					}

					// https://discord.com/developers/docs/topics/gateway-events#interaction-create
					case INTERACTION_CREATE -> {
						System.out.println(d);
						final var bot = (BotDiscordClient) client;
						bot.onInteractionCreate(Interaction.construct(bot, d));
					}

					case GUILD_AUDIT_LOG_ENTRY_CREATE ->
						client.onGuildAuditLogEntryCreate(new AuditLogEntry(client, d));

					case GUILD_CREATE -> client.onGuildCreate(client.guilds.cache(d));
					case GUILD_UPDATE -> client.onGuildUpdate(client.guilds.cache(d));
					case GUILD_DELETE -> {
						final var id = d.getString("id");
						final var deletedGuild = client.guilds.cache.get(id);
						deletedGuild.markAsDeleted();
						client.onGuildDelete(deletedGuild);
					}

					case CHANNEL_CREATE -> client.onChannelCreate(client.channels.cache(d));
					case CHANNEL_UPDATE -> client.onChannelUpdate(client.channels.cache(d));
					case CHANNEL_DELETE -> {
						final var id = d.getString("id");
						final var deletedChannel = client.channels.cache.get(id);
						deletedChannel.markAsDeleted();
						client.onChannelDelete(deletedChannel);
					}

					case MESSAGE_CREATE -> {
						client.channels.get(d.getString("channel_id")).thenAccept(c -> {
							final var message = ((MessageChannel) c).getMessageManager().cache(d);
							client.onMessageCreate(message);
						});
					}

					case MESSAGE_UPDATE -> {
						client.channels.get(d.getString("channel_id")).thenAccept(c -> {
							final var message = ((MessageChannel) c).getMessageManager().cache(d);
							client.onMessageUpdate(message);
						});
					}

					case MESSAGE_DELETE -> {
						client.channels.get(d.getString("channel_id")).thenAccept(c -> {
							final var deletedMessage = ((MessageChannel) c).getMessageManager().cache.get(d.getString("id"));
							deletedMessage.markAsDeleted();
							client.onMessageDelete(deletedMessage);
						});
					}

					default -> {}
				}
			}

			default -> {}
		}
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
}
