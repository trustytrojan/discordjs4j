package discord.structures;

import discord.client.DiscordClient;
import simple_json.JSONObject;

public class Application implements DiscordResource {

	private final DiscordClient client;
	private JSONObject data;

	public final User owner;

	public Application(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
		owner = client.users.fetch(data.getObject("owner").getString("id")).join();
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}
	
	// https://discord.com/developers/docs/resources/application

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public void setData(JSONObject data) {
		this.data = data;
	}

}
