package discord.managers.guild;

import discord.client.DiscordClient;
import discord.managers.ResourceManager;
import discord.structures.Guild;
import discord.structures.GuildResource;

public abstract class GuildResourceManager<T extends GuildResource> extends ResourceManager<T> {
	public final Guild guild;

	protected GuildResourceManager(final DiscordClient client, final Guild guild) {
		super(client);
		this.guild = guild;
	}
}
