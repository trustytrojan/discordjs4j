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
import discord.structures.interactions.Interaction;
// import discord.structures.Presence;
import discord.util.JSON;
import discord.util.RunnableRepeater;

public final class GatewayClient extends WebSocketClient {
	
	private final DiscordClient client;
	public final RunnableRepeater repeater = new RunnableRepeater();
	private long sequence_number;
	private long heartbeatSentAt;
	private long ping;

	public GatewayClient(DiscordClient client) throws URISyntaxException {
		super(new URI("wss://gateway.discord.gg/"));
		this.client = client;
	}

	public long ping() {
		return ping;
	}

	public void login(String token, GatewayIntent[] intents) throws Exception {
		connectBlocking();

		send(JSON.objectFrom(
			JSON.objectEntry("op", GatewayOpcode.Identify.value),
			JSON.objectEntry("d", JSON.objectFrom(
				JSON.objectEntry("token", token),
				JSON.objectEntry("intents", GatewayIntent.sum(intents)),
				JSON.objectEntry("properties", JSON.objectFrom(
					JSON.objectEntry("os", System.getProperty("os.name")),
					JSON.objectEntry("browser", "discord-java"),
					JSON.objectEntry("device", "discord-java")
				))
			))
		).toString());
	}

	@Override
	public void onOpen(ServerHandshake handshake) {
		System.out.printf(
			"GatewayClient: Connection to Discord gateway opened\n  Status Code: %d\n  Status Message: %s\n  Content: %s\n",
			handshake.getHttpStatus(),
			handshake.getHttpStatusMessage(),
			handshake.getContent()
		);
	}

	@Override
	public void onMessage(String __) { try {
		final var obj = JSON.parseObject(__);
		final var opcode = GatewayOpcode.get(obj.getLong("op"));
		
		switch (opcode) {

			case Dispatch -> {
				sequence_number = obj.getLong("s");
				
				final var t = obj.getString("t");
				System.out.printf("[GatewayClient] Event received: %s\n", t);

				switch (GatewayEvent.get(t)) {

					case Ready -> {
						final var d = obj.getObject("d");
						client.user = (ClientUser)client.users.cache(new ClientUser(client, d.getObject("user")));
						client.ready.emit();
					}

					case InteractionCreate -> ((BotDiscordClient)client).interactionCreate.emit(Interaction.createCorrectInteraction(client, obj.getObject("d")));

					case GuildAuditLogEntryCreate -> client.auditLogEntryCreate.emit(new AuditLogEntry(client, obj.getObject("d")));

					case GuildCreate -> client.guildCreate.emit(client.guilds.cache(obj.getObject("d")));
					case GuildUpdate -> client.guildUpdate.emit(client.guilds.cache(obj.getObject("d")));
					case GuildDelete -> {
						final var id = obj.getObject("d").getString("id");
						final var removed = client.guilds.cache.remove(id);
						if (removed == null) return;
						client.guildDelete.emit(removed);
					}

					case ChannelCreate -> client.channelCreate.emit(client.channels.cache(obj.getObject("d")));
					case ChannelUpdate -> client.channelUpdate.emit(client.channels.cache(obj.getObject("d")));
					case ChannelDelete -> {
						final var id = obj.getObject("d").getString("id");
						final var removed = client.channels.cache.remove(id);
						if (removed == null) return;
						client.channelDelete.emit(removed);
					}

					case MessageCreate -> client.messageCreate.emit(new Message(client, obj.getObject("d")));
					case MessageUpdate -> {
						final var d = obj.getObject("d");
						client.channels.fetch(d.getString("channel_id")).thenAccept((channel) -> {
							final var messages = ((TextBasedChannel)channel).messages();
							client.messageUpdate.emit(messages.cache(d));
						});
					}
					case MessageDelete -> {
						final var d = obj.getObject("d");
						client.channels.fetch(d.getString("channel_id")).thenAccept((channel) -> {
							final var messages = ((TextBasedChannel)channel).messages();
							final var removed = messages.cache.remove(d.getString("id"));
							if (removed == null) return;
							client.messageDelete.emit(removed);
						});
					}
					
					default -> {
						final var str = obj.get("d").toString();
						if (str.length() < 1000) System.out.println(str);
					}

				}
			}

			case HeartbeatACK -> {
				ping = System.currentTimeMillis() - heartbeatSentAt;
				System.out.printf("[GatewayClient] Heartbeat ACK received; Ping: %sms\n", ping);
			}

			case Hello -> {
				final var d = obj.getObject("d");

				// Interval in milliseconds that Discord wants us to wait before
				// sending another heartbeat.
				final var heartbeat_interval = d.getLong("heartbeat_interval");

				repeater.repeat(() -> {
					System.out.printf("[GatewayClient] Sending heartbeat; Sequence number: %d\n", sequence_number);
					this.send(JSON.objectFrom(
						JSON.objectEntry("op", GatewayOpcode.Heartbeat.value),
						JSON.objectEntry("d", sequence_number)
					).toString());
					heartbeatSentAt = System.currentTimeMillis();
				}, heartbeat_interval);
			}

			default -> {}
			
		}
	} catch (Exception e) { e.printStackTrace(); } }

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.printf("[GatewayClient] Connection closed!\n  Code: %d\n  Reason: %s\n  Remote: %b\n", code, reason, remote);
	}

	@Override
	public void onError(Exception e) {
		System.err.println("[GatewayClient] WebSocket error!");
		e.printStackTrace();
	}

}
