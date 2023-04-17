package discord.structures;

import discord.client.DiscordClient;
import discord.util.CDN;
import discord.util.CDN.URLFactory;
import simple_json.JSONObject;

public class Application implements DiscordResource {
	private final DiscordClient client;
	private JSONObject data;

	public final User owner;

	public Application(final DiscordClient client, final JSONObject data) {
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

	public final URLFactory icon = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("icon");
		}

		@Override
		public String url(int size, String extension) {
			return CDN.applicationIcon(id(), extension, size, extension);
		}
	};
	
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
	public void setData(final JSONObject data) {
		this.data = data;
	}

	@Override
	public String apiPath() {
		return "/applications/" + id();
	}
}
