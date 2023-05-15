package discord.structures.channels;

import discord.client.DiscordClient;
import discord.structures.AbstractDiscordResource;
import discord.structures.Guild;
import simple_json.SjObject;

public class CategoryChannel extends AbstractDiscordResource implements GuildChannel {
	private final Guild guild;

	public CategoryChannel(DiscordClient client, SjObject data) {
		super(client, data);
		guild = client.guilds.fetch(guildId()).join();
	}

	@Override
	public Guild guild() {
		return guild;
	}
}
