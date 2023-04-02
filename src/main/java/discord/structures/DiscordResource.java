package discord.structures;

import discord.client.DiscordClient;
import simple_json.JSONObject;

public interface DiscordResource extends Identifiable {

	DiscordClient client();

	JSONObject getData();

	void setData(JSONObject data);

	@Override
	default String id() {
		return getData().getString("id");
	}

}
