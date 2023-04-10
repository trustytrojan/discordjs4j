package discord.managers;

import discord.client.DiscordClient;
import discord.structures.Guild;
import simple_json.JSONObject;

public class GuildManager extends DataManager<Guild> {

	public GuildManager(DiscordClient client) {
		super(client);
	}

	@Override
	public Guild construct(JSONObject data) {
		return new Guild(client, data);
	}

	@Override
	public Guild fetch(String id, boolean force) {
		return super.fetch(id, "/guilds/" + id, force);
	}

}
