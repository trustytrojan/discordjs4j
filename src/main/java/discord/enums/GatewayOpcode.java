package discord.enums;

public enum GatewayOpcode {
	Dispatch(0),
	Heartbeat(1),
	Identify(2),
	PresenceUpdate(3),
	VoiceStateUpdate(4),
	Resume(6),
	Reconnect(7),
	RequestGuildMembers(8),
	InvalidSession(9),
	Hello(10),
	HeartbeatACK(11);

	public static GatewayOpcode get(long value) {
		return get((int)value);
	}

	public static GatewayOpcode get(int value) {
		for (final var x : GatewayOpcode.values()) if (x.value == value) return x;
		return null;
	}

	public final int value;

	private GatewayOpcode(int value) {
		this.value = value;
	}
}
