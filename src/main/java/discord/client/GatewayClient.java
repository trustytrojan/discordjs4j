package discord.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import discord.enums.GatewayEvent;
import discord.enums.GatewayIntent;
import discord.enums.GatewayOpcode;
import discord.structures.AuditLogEntry;
import discord.structures.ClientUser;
import discord.structures.Message;
import discord.structures.channels.TextBasedChannel;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;
import discord.util.RunnableRepeater;
import simple_json.JSON;
import simple_json.JSONObject;

public class GatewayClient extends WebSocketClient {
	private static final URI GATEWAY_URI;

	static {
		try {
			GATEWAY_URI = new URI("wss://gateway.discord.gg/");
		} catch (URISyntaxException e) {
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
			if (!connectBlocking()) {
				System.exit(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		final var str = """
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
				System.getProperty("os.name"));

		send(str);
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
	public void onMessage(String __) {
		final var obj = JSON.parseObject(__);
		final var opcode = GatewayOpcode.resolve(obj.getLong("op"));

		switch (opcode) {
			case DISPATCH -> {
				sequenceNumber = obj.getLong("s");

				final var t = obj.getString("t");
				System.out.printf("[GatewayClient] Event received: %s\n", t);

				final var event = GatewayEvent.valueOf(t);
				if (event == null) {
					return;
				}

				switch (event) {
					case READY -> {
						client.user = new ClientUser(client, obj.getObject("d").getObject("user"));
						client.users.cache.put(client.user);
						client.ready.emit();
					}

					case INTERACTION_CREATE -> {
						final var bot = (BotDiscordClient) client;
						final var d = obj.getObject("d");
						switch (Interaction.Type.resolve(d.getLong("type"))) {
							case APPLICATION_COMMAND ->
								bot.chatInputInteractionCreate.emit(new ChatInputInteraction(bot, obj.getObject("d")));
							default -> {
							}
						}
					}

					case GUILD_AUDIT_LOG_ENTRY_CREATE ->
						client.auditLogEntryCreate.emit(new AuditLogEntry(client, obj.getObject("d")));

					case GUILD_CREATE -> client.guildCreate.emit(client.guilds.cache(obj.getObject("d")));
					case GUILD_UPDATE -> client.guildUpdate.emit(client.guilds.cache(obj.getObject("d")));
					case GUILD_DELETE -> {
						final var id = obj.getObject("d").getString("id");
						final var removed = client.guilds.cache.remove(id);
						if (removed == null)
							return;
						client.guildDelete.emit(removed);
					}

					case CHANNEL_CREATE -> client.channelCreate.emit(client.channels.cache(obj.getObject("d")));
					case CHANNEL_UPDATE -> client.channelUpdate.emit(client.channels.cache(obj.getObject("d")));
					case CHANNEL_DELETE -> {
						final var id = obj.getObject("d").getString("id");
						final var removed = client.channels.cache.remove(id);
						if (removed == null)
							return;
						client.channelDelete.emit(removed);
					}

					case MESSAGE_CREATE -> {
						final var message = new Message(client, obj.getObject("d"));
						message.channel.messages().cache.put(message);
						client.messageCreate.emit(message);
					}
					case MESSAGE_UPDATE -> {
						final var message = new Message(client, obj.getObject("d"));
						message.channel.messages().cache.put(message);
						client.messageUpdate.emit(message);
					}
					case MESSAGE_DELETE -> {
						final var d = obj.getObject("d");
						final var channel = (TextBasedChannel) client.channels.fetch(d.getString("channel_id"));
						final var message = channel.messages().cache.remove(d.getString("id"));
						if (message != null) {
							client.messageDelete.emit(message);
						}
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
					final var heartbeat = new JSONObject();
					heartbeat.put("op", GatewayOpcode.HEARTBEAT.value);
					heartbeat.put("d", sequenceNumber);
					send(heartbeat.toString());
					heartbeatSentAt = System.currentTimeMillis();
				}, heartbeat_interval);
			}

			default -> {
			}
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.printf("[GatewayClient] Connection closed!\n  Code: %d\n  Reason: %s\n  Remote: %b\n", code, reason,
				remote);
		System.exit(1);
	}

	@Override
	public void onError(Exception e) {
		System.err.println("[GatewayClient] WebSocket error!");
		e.printStackTrace();
	}
}