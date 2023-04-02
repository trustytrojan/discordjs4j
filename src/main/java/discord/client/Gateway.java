package discord.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import discord.structures.AuditLog;
import discord.structures.ClientUser;
import discord.structures.Message;
import discord.structures.channels.TextBasedChannel;
import discord.structures.interactions.Interaction;
import discord.util.RunnableRepeater;
import simple_json.JSON;
import simple_json.JSONObject;

public final class Gateway {

    public static enum Event {
        READY,
        INTERACTION_CREATE,
        GUILD_AUDIT_LOG_ENTRY_CREATE,
        GUILD_CREATE,
        GUILD_UPDATE,
        GUILD_DELETE,
        CHANNEL_CREATE,
        CHANNEL_UPDATE,
        CHANNEL_DELETE,
        MESSAGE_CREATE,
        MESSAGE_UPDATE,
        MESSAGE_DELETE,
        USER_UPDATE;
    
        public static final Event get(String value) {
            for (final var x : Event.values())
                if (x.name().equals(value))
                    return x;
            return null;
        }
    }

    public static enum Intent {
        GUILDS(1 << 0),
        GUILD_MEMBERS(1 << 1),
        GUILD_MODERATION(1 << 2),
        GUILD_EMOJIS_AND_STICKERS(1 << 3),
        GUILD_INTEGRATIONS(1 << 4),
        GUILD_WEBHOOKS(1 << 5),
        GUILD_INVITES(1 << 6),
        GUILD_VOICE_STATES(1 << 7),
        GUILD_PRESENCES(1 << 8),
        GUILD_MESSAGES(1 << 9),
        GUILD_MESSAGE_REACTIONS(1 << 10),
        GUILD_MESSAGE_TYPING(1 << 11),
        DIRECT_MESSAGES(1 << 12),
        DIRECT_MESSAGE_REACTIONS(1 << 13),
        DIRECT_MESSAGE_TYPING(1 << 14),
        MESSAGE_CONTENT(1 << 15),
        GUILD_SCHEDULED_EVENTS(1 << 16),
        AUTO_MODERATION_CONFIGURATION(1 << 20),
        AUTO_MODERATION_EXECUTION(1 << 21);
    
        public static int sum(Intent... intents) {
            var sum = 0;
            for (final var i : intents)
                sum += i.value;
            return sum;
        }
    
        public final int value;
    
        private Intent(int value) {
            this.value = value;
        }
    }
    
    public static enum Opcode {
        DISPATCH(0),
        HEARTBEAT(1),
        IDENTIFY(2),
        PRESENCE_UPDATE(3),
        VOICE_STATE_UPDATE(4),
        RESUME(6),
        RECONNECT(7),
        REQUEST_GUILD_MEMBERS(8),
        INVALID_SESSION(9),
        HELLO(10),
        HEARTBEAT_ACK(11);
    
        public static Opcode resolve(long value) {
            for (final var x : Opcode.values())
                if (x.value == value)
                    return x;
            return null;
        }
    
        public final int value;
    
        private Opcode(int value) {
            this.value = value;
        }
    }

    public static class Client extends WebSocketClient {

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
    
        public Client(DiscordClient client) {
            super(GATEWAY_URI);
            this.client = client;
        }
    
        public long ping() {
            return ping;
        }
    
        public void login(String token, Intent[] intents) {
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
                    Opcode.IDENTIFY.value,
                    token,
                    Intent.sum(intents),
                    System.getProperty("os.name")
                );
    
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
            final var opcode = Opcode.resolve(obj.getLong("op"));
    
            switch (opcode) {
    
                case DISPATCH -> {
                    sequenceNumber = obj.getLong("s");
    
                    final var t = obj.getString("t");
                    System.out.printf("[GatewayClient] Event received: %s\n", t);
    
                    final var event = Event.valueOf(t);
                    if (event == null) {
                        return;
                    }
    
                    switch (event) {
    
                        case READY -> {
                            client.user = new ClientUser(client, obj.getObject("d").getObject("user"));
                            client.users.cache.put(client.user);
                            client.ready.emit();
                        }
    
                        case INTERACTION_CREATE -> ((DiscordClient.Bot) client).interactionCreate
                            .emit(Interaction.createCorrectInteraction(client, obj.getObject("d")));
    
                        case GUILD_AUDIT_LOG_ENTRY_CREATE ->
                            client.auditLogEntryCreate.emit(new AuditLog.Entry(client, obj.getObject("d")));
    
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
                        heartbeat.put("op", Opcode.HEARTBEAT.value);
                        heartbeat.put("d", sequenceNumber);
                        send(heartbeat.toString());
                        heartbeatSentAt = System.currentTimeMillis();
                    }, heartbeat_interval);
                }
    
                default -> {}
    
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
    
}
