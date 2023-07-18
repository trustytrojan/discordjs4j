package discord.resources;

import discord.client.DiscordClient;
import sj.SjObject;

public abstract class AbstractGuildResource extends AbstractDiscordResource implements GuildResource {
	protected final Guild guild;

	protected AbstractGuildResource(DiscordClient client, SjObject data) {
		super(client, data);
		guild = client.guilds.get(guildId()).join();
	}

	@Override
	public Guild guild() {
		return guild;
	}
}
