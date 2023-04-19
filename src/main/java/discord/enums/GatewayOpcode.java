package discord.enums;

public enum GatewayOpcode {
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

	public static GatewayOpcode resolve(final short value) {
		for (final var x : GatewayOpcode.values())
			if (x.value == value)
				return x;
		return null;
	}

	public final short value;

	private GatewayOpcode(final int value) {
		this.value = (short) value;
	}
}
