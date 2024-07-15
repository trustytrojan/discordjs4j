package discord.enums;

import java.util.stream.Stream;

public enum GatewayOpcode {
	DISPATCH, // 0
	HEARTBEAT, // 1
	IDENTIFY, // 2
	UPDATE_PRESENCE, // 3
	VOICE_STATE_UPDATE, // 4
	RESUME(6),
	RECONNECT(7),
	REQUEST_GUILD_MEMBERS(8),
	INVALID_SESSION(9),
	HELLO(10),
	HEARTBEAT_ACK(11);

	private static final GatewayOpcode[] LOOKUP_TABLE;

	static {
		final var values = GatewayOpcode.values();
		LOOKUP_TABLE = new GatewayOpcode[values[values.length - 1].value + 1];
		Stream.of(values).forEach(v -> LOOKUP_TABLE[v.value] = v);
	}

	public static GatewayOpcode resolve(int value) {
		return LOOKUP_TABLE[value];
	}

	public final int value;

	private GatewayOpcode() {
		value = ordinal();
	}

	private GatewayOpcode(int value) {
		this.value = value;
	}
}
