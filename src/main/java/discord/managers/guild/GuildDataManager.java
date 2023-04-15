package discord.managers.guild;

import discord.client.DiscordClient;
import discord.managers.ResourceManager;
import discord.structures.Guild;
import discord.structures.GuildObject;

public abstract class GuildDataManager<T extends GuildObject> extends ResourceManager<T> {
	public final Guild guild;

	protected GuildDataManager(DiscordClient client, Guild guild) {
		super(client);
		this.guild = guild;
	}
}
