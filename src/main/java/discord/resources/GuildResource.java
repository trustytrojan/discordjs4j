package discord.resources;

import java.util.concurrent.CompletableFuture;

import discord.resources.guilds.Guild;

public interface GuildResource extends DiscordResource {
	String getGuildId();

	default CompletableFuture<Guild> getGuild() {
		return getClient().guilds.get(getGuildId());
	}
}
