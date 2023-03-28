package discord.structures;

import com.alibaba.fastjson2.JSONObject;

import discord.client.DiscordClient;

public interface DiscordObject {

	/**
	 * Allows default method implementations of interfaces
	 * to access the data of this Discord object.
	 */
	JSONObject getData();

	void setData(JSONObject data);

	DiscordClient client();

	default String id() {
		return getData().getString("id");
	}

}
