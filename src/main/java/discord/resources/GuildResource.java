package discord.resources;

import discord.resources.guilds.Guild;

public interface GuildResource extends DiscordResource {
	Guild guild();

	default String guildId() {
		return getData().getString("guild_id");
	}
}
