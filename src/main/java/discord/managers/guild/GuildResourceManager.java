package discord.managers.guild;

import discord.client.DiscordClient;
import discord.managers.ResourceManager;
import discord.resources.GuildResource;
import discord.resources.guilds.Guild;

public abstract class GuildResourceManager<T extends GuildResource> extends ResourceManager<T> {
	public final Guild guild;

	protected GuildResourceManager(DiscordClient client, Guild guild) {
		super(client);
		this.guild = guild;
	}
}
