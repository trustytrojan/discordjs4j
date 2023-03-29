package discord.structures;

import discord.client.DiscordClient;
import simple_json.JSONObject;

public interface DiscordObject {

	JSONObject getData();

	void setData(JSONObject data);

	DiscordClient client();

	default String id() {
		return getData().getString("id");
	}

}
