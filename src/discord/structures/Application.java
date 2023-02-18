package discord.structures;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;

public class Application implements DiscordObject {
	
	private final DiscordClient client;
	private BetterJSONObject data;

	public Application(DiscordClient client) {
		this.client = client;
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}

	// there is more, but i don't care for now
	// https://discord.com/developers/docs/resources/application

	@Override
	public BetterJSONObject getData() {
		return data;
	}

	@Override
	public void setData(BetterJSONObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public String api_path() {
		return "/applications/@me";
	}

}
