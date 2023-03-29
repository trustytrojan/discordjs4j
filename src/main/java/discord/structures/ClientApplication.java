package discord.structures;

import com.alibaba.fastjson2.JSONObject;

import discord.client.DiscordClient;

public class ClientApplication implements DiscordObject {

	private final DiscordClient client;
	public JSONObject data;

	public ClientApplication(DiscordClient client) {
		this.client = client;
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}

	public User owner() {
		return client.users.fetch(data.getJSONObject("owner").getString("id"));
	}

	// there is more, but i don't care for now
	// https://discord.com/developers/docs/resources/application

	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public void setData(JSONObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

}
