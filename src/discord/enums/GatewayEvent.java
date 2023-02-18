package discord.enums;

public enum GatewayEvent {
	Ready("READY"),
	InteractionCreate("INTERACTION_CREATE"),
	GuildAuditLogEntryCreate("GUILD_AUDIT_LOG_ENTRY_CREATE"),
	GuildCreate("GUILD_CREATE"),
	GuildUpdate("GUILD_UPDATE"),
	GuildDelete("GUILD_DELETE"),
	ChannelCreate("CHANNEL_CREATE"),
	ChannelUpdate("CHANNEL_UPDATE"),
	ChannelDelete("CHANNEL_DELETE"),
	MessageCreate("MESSAGE_CREATE"),
	MessageUpdate("MESSAGE_UPDATE"),
	MessageDelete("MESSAGE_DELETE");

	public static final GatewayEvent get(String value) {
		for(final var x : GatewayEvent.values())
			if(x.value.equals(value)) return x;
		return null;
	}

	public final String value;

	private GatewayEvent(String value) {
		this.value = value;
	}
}
