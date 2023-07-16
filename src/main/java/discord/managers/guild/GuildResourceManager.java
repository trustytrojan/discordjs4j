package discord.managers.guild;

import discord.client.DiscordClient;
import discord.managers.ResourceManager;
import discord.resources.Guild;
import discord.resources.GuildResource;

public abstract class GuildResourceManager<T extends GuildResource> extends ResourceManager<T> {
	public final Guild guild;

	protected GuildResourceManager(DiscordClient client, Guild guild) {
		super(client);
		this.guild = guild;
	}
}
