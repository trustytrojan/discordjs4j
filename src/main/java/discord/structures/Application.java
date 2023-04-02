package discord.structures;

import discord.client.DiscordClient;
import simple_json.JSONObject;

public class Application implements DiscordResource {

	private final DiscordClient client;
	private final JSONObject data;

	public Application(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}

	public User owner() {
		return client.users.fetch(data.getObject("owner").getString("id"));
	}

	// https://discord.com/developers/docs/resources/application

	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

}
