package discord.structures;

import discord.client.DiscordClient;
import discord.util.CDN;
import discord.util.CDN.URLFactory;
import simple_json.JSONObject;

public class User implements DiscordResource {
	private final DiscordClient client;
	private JSONObject data;

	public User(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
	}

	public String mention() {
		return "<@" + id() + '>';
	}

	public String username() {
		return data.getString("username");
	}

	public short discriminator() {
		return Short.parseShort(data.getString("discriminator"));
	}

	public String tag() {
		return username() + '#' + discriminator();
	}

	public boolean isBot() {
		final var bot = data.getBoolean("bot");
		return (bot == null) ? false : bot;
	}

	public final URLFactory avatar = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("avatar");
		}

		@Override
		public String url(int size, String extension) {
			final var hash = hash();
			if (hash != null)
				return CDN.userAvatar(id(), hash, size, extension);
			return CDN.defaultUserAvatar(discriminator());
		}
	};

	public final URLFactory banner = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("banner");
		}

		@Override
		public String url(int size, String extension) {
			return CDN.guildOrUserBanner(id(), hash(), size, extension);
		}
	};

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

	@Override
	public String apiPath() {
		return "/users/" + id();
	}
}
