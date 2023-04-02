package discord.structures;

import discord.client.DiscordClient;
import simple_json.JSONObject;

public interface DiscordResource {

	JSONObject getData();

	DiscordClient client();

	default String id() {
		return getData().getString("id");
	}

}
