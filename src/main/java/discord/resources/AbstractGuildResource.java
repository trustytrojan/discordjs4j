package discord.resources;

import discord.client.DiscordClient;
import discord.resources.guilds.Guild;
import sj.SjObject;

public abstract class AbstractGuildResource extends AbstractDiscordResource implements GuildResource {
	protected final Guild guild;

	protected AbstractGuildResource(DiscordClient client, SjObject data, String guildApiPath) {
		super(client, data, "/guilds/" + guildApiPath);
		guild = client.guilds.get(guildId()).join();
	}

	@Override
	public Guild guild() {
		return guild;
	}
}
