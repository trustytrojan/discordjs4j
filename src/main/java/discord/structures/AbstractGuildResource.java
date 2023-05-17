package discord.structures;

import discord.client.DiscordClient;
import simple_json.SjObject;

public abstract class AbstractGuildResource extends AbstractDiscordResource implements GuildResource {
	protected final Guild guild;

	protected AbstractGuildResource(DiscordClient client, SjObject data) {
		super(client, data);
		guild = client.guilds.fetch(guildId()).join();
	}

	@Override
	public Guild guild() {
		return guild;
	}
}
