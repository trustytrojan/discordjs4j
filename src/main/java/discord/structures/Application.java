package discord.structures;

import discord.client.DiscordClient;
import discord.util.CDN;
import discord.util.CDN.URLFactory;
import sj.SjObject;

public class Application extends AbstractDiscordResource {
	public final User owner;

	public Application(DiscordClient client, SjObject data) {
		super(client, data);
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
			return CDN.applicationIcon(id, extension, size, extension);
		}
	};
	
	// https://discord.com/developers/docs/resources/application

	@Override
	public String apiPath() {
		return "/applications/" + id;
	}
}
