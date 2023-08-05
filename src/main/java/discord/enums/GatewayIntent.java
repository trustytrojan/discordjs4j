package discord.enums;

import java.util.stream.Stream;

public enum GatewayIntent {
	GUILDS,
	GUILD_MEMBERS,
	GUILD_MODERATION,
	GUILD_EMOJIS_AND_STICKERS,
	GUILD_INTEGRATIONS,
	GUILD_WEBHOOKS,
	GUILD_INVITES,
	GUILD_VOICE_STATES,
	GUILD_PRESENCES,
	GUILD_MESSAGES,
	GUILD_MESSAGE_REACTIONS,
	GUILD_MESSAGE_TYPING, 
	DIRECT_MESSAGES,
	DIRECT_MESSAGE_REACTIONS,
	DIRECT_MESSAGE_TYPING,
	MESSAGE_CONTENT,
	GUILD_SCHEDULED_EVENTS,
	AUTO_MODERATION_CONFIGURATION(20),
	AUTO_MODERATION_EXECUTION(21);

	public static int sum(GatewayIntent... intents) {
		return Stream.of(intents).mapToInt(i -> i.value).sum();
	}

	private final int value;

	private GatewayIntent() {
		value = (1 << ordinal());
	}

	private GatewayIntent(int bitshift) {
		value = (1 << bitshift);
	}
}
