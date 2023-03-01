package log_bot;

import discord.structures.interactions.Interaction;

import static log_bot.Main.client;

public class Util {
	
	static String formatChannel(String channel) {
		return (channel != null) ? "<#"+channel+'>' : "(none)";
	}

	static TGuild getTGuild(Interaction interaction) {
		final var guildId = interaction.guildId();
		return client.tguilds.ensure(guildId, () -> new TGuild(guildId));
	}

}
