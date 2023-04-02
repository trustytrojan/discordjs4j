package discord.structures.channels;

import discord.client.DiscordClient;
import discord.structures.Guild;
import simple_json.JSONObject;

public class CategoryChannel implements GuildChannel {

	private final DiscordClient client;
	private JSONObject data;

	public final Guild guild;

	public CategoryChannel(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
		guild = client.guilds.fetch(guildId());
	}

	@Override
	public Guild guild() {
		return guild;
	}

	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

}
