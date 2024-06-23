package discord.managers;

import java.util.Objects;

import discord.client.DiscordClient;
import discord.resources.GuildResource;
import discord.resources.guilds.Guild;

public abstract class GuildResourceManager<T extends GuildResource> extends ResourceManager<T> {
	protected final Guild guild;

	protected GuildResourceManager(DiscordClient client, Guild guild, String path) {
		super(client, "/guilds/" + guild.getId() + path);
		this.guild = Objects.requireNonNull(guild);
	}
}
