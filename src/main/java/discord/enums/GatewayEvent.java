package discord.enums;

public enum GatewayEvent {
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

	public static final GatewayEvent get(String value) {
		for (final var x : GatewayEvent.values())
			if (x.name().equals(value))
				return x;
		return null;
	}
}
