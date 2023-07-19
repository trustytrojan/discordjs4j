package discord.enums;

public enum GatewayOpcode {
	DISPATCH,
	HEARTBEAT,
	IDENTIFY,
	PRESENCE_UPDATE,
	VOICE_STATE_UPDATE,
	RESUME(6),
	RECONNECT(7),
	REQUEST_GUILD_MEMBERS(8),
	INVALID_SESSION(9),
	HELLO(10),
	HEARTBEAT_ACK(11);

	public static GatewayOpcode resolve(int value) {
		for (final var x : GatewayOpcode.values())
			if (x.value == value)
				return x;
		return null;
	}

	public final int value;

	private GatewayOpcode() {
		value = ordinal();
	}

	private GatewayOpcode(int value) {
		this.value = value;
	}
}
