package discord.structures.channels;

import discord.client.DiscordClient;
import discord.structures.Guild;
import simple_json.SjObject;

public class CategoryChannel implements GuildChannel {
	private final DiscordClient client;
	private SjObject data;

	private final Guild guild;

	public CategoryChannel(final DiscordClient client, final SjObject data) {
		this.client = client;
		setData(data);
		guild = client.guilds.fetch(guildId()).join();
	}

	@Override
	public SjObject getData() {
		return data;
	}

	@Override
	public void setData(SjObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public Guild guild() {
		return guild;
	}
}
