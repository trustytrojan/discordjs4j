package discord.enums;

public enum GatewayEvent {
	READY("READY"),
	INTERACTION_CREATE("INTERACTION_CREATE"),
	GUILD_AUDIT_LOG_ENTRY_CREATE("GUILD_AUDIT_LOG_ENTRY_CREATE"),
	GUILD_CREATE("GUILD_CREATE"),
	GUILD_UPDATE("GUILD_UPDATE"),
	GUILD_DELETE("GUILD_DELETE"),
	CHANNEL_CREATE("CHANNEL_CREATE"),
	CHANNEL_UPDATE("CHANNEL_UPDATE"),
	CHANNEL_DELETE("CHANNEL_DELETE"),
	MESSAGE_CREATE("MESSAGE_CREATE"),
	MESSAGE_UPDATE("MESSAGE_UPDATE"),
	MESSAGE_DELETE("MESSAGE_DELETE");

	public static final GatewayEvent get(String value) {
		for (final var x : GatewayEvent.values())
			if (x.value.equals(value))
				return x;
		return null;
	}

	public final String value;

	private GatewayEvent(String value) {
		this.value = value;
	}
}
