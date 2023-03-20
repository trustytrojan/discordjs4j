package discord.client;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import discord.enums.GatewayEvent;
import discord.enums.GatewayIntent;
import discord.enums.GatewayOpcode;
import discord.structures.AuditLogEntry;
import discord.structures.ClientUser;
import discord.structures.Message;
import discord.structures.channels.TextBasedChannel;
import discord.structures.interactions.Interaction;
import discord.util.JSON;
import discord.util.RunnableRepeater;

public final class GatewayClient extends WebSocketClient {

	private static final URI GATEWAY_URI;

	static {
		try {
			GATEWAY_URI = new URI("wss://gateway.discord.gg/");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private final DiscordClient client;
	public final RunnableRepeater repeater = new RunnableRepeater();
	private long sequenceNumber;
	private long heartbeatSentAt;
	private long ping;

	public GatewayClient(DiscordClient client) {
		super(GATEWAY_URI);
		this.client = client;
	}

	public long ping() {
		return ping;
	}

	public void login(String token, GatewayIntent[] intents) {
		try {
			connectBlocking();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		final var obj = JSON.objectFrom(
			JSON.objectEntry("op", GatewayOpcode.IDENTIFY.value),
			JSON.objectEntry("d", JSON.objectFrom(
				JSON.objectEntry("token", token),
				JSON.objectEntry("intents", GatewayIntent.sum(intents)),
				JSON.objectEntry("properties", JSON.objectFrom(
					JSON.objectEntry("os", System.getProperty("os.name")),
					JSON.objectEntry("browser", "discordjs4j"),
					JSON.objectEntry("device", "discordjs4j")
				))
			))
		);

		send(obj.toString());
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
			handshake.getContent()
		);
	}

	@Override
	public void onMessage(String __) {
		try {
			final var obj = JSON.parseObject(__);
			final var opcode = GatewayOpcode.resolve(obj.getLong("op"));
			
			switch (opcode) {

				case DISPATCH -> {
					sequenceNumber = obj.getLong("s");
					
					final var t = obj.getString("t");
					System.out.printf("[GatewayClient] Event received: %s\n", t);

					final var gatewayEvent = GatewayEvent.valueOf(t);
					if (gatewayEvent == null)
						return;

					switch (gatewayEvent) {

						case READY -> {
							final var d = obj.getObject("d");
							client.user = (ClientUser)client.users.cache(new ClientUser(client, d.getObject("user")));
							client.ready.emit();
						}

						case INTERACTION_CREATE -> ((BotDiscordClient)client).interactionCreate.emit(Interaction.createCorrectInteraction(client, obj.getObject("d")));

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

						case MESSAGE_CREATE -> client.messageCreate.emit(new Message(client, obj.getObject("d")));
						case MESSAGE_UPDATE -> {
							final var d = obj.getObject("d");
							client.channels.fetch(d.getString("channel_id")).thenAccept((channel) -> {
								final var messages = ((TextBasedChannel)channel).messages();
								client.messageUpdate.emit(messages.cache(d));
							});
						}
						case MESSAGE_DELETE -> {
							final var d = obj.getObject("d");
							client.channels.fetch(d.getString("channel_id")).thenAccept((channel) -> {
								final var messages = ((TextBasedChannel)channel).messages();
								final var removed = messages.cache.remove(d.getString("id"));
								if (removed == null) return;
								client.messageDelete.emit(removed);
							});
						}
						
						default -> {
							final var dataString = obj.get("d").toString();
							if (dataString.length() < 1000)
								System.out.println(dataString);
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

					repeater.repeat(() -> {
						System.out.printf("[GatewayClient] Sending heartbeat; Sequence number: %d\n", sequenceNumber);
						send(JSON.objectFrom(
							JSON.objectEntry("op", GatewayOpcode.HEARTBEAT.value),
							JSON.objectEntry("d", sequenceNumber)
						).toString());
						heartbeatSentAt = System.currentTimeMillis();
					}, heartbeat_interval);
				}

				default -> {}
				
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.printf("[GatewayClient] Connection closed!\n  Code: %d\n  Reason: %s\n  Remote: %b\n", code, reason, remote);
		System.exit(1);
	}

	@Override
	public void onError(Exception e) {
		System.err.println("[GatewayClient] WebSocket error!");
		e.printStackTrace();
	}

}
