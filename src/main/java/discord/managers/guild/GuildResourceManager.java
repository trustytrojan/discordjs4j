package discord.managers.guild;

import java.util.Objects;

import discord.client.DiscordClient;
import discord.managers.ResourceManager;
import discord.resources.GuildResource;
import discord.resources.guilds.Guild;

public abstract class GuildResourceManager<T extends GuildResource> extends ResourceManager<T> {
	protected final Guild guild;

	protected GuildResourceManager(DiscordClient client, Guild guild, String guildPath) {
		super(client, "/guilds/" + guild.id + guildPath);
		this.guild = Objects.requireNonNull(guild);
	}
}
