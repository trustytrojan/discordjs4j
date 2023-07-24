package discord.resources.guilds;

import discord.client.DiscordClient;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.URLFactory;
import sj.SjObject;

public class PreviewGuild extends BaseGuild {
	public PreviewGuild(DiscordClient client, SjObject data) {
		super(client, data);
	}

	public final URLFactory splash = new URLFactory() {
		@Override
		public String getHash() {
			return data.getString("splash");
		}

		@Override
		public String makeURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeGuildSplashURL(id, getHash(), size, extension);
		}
	};

	public final URLFactory discoverySplash = new URLFactory() {
		@Override
		public String getHash() {
			return data.getString("discovery_splash");
		}

		@Override
		public String makeURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeGuildDiscoverySplashURL(id, getHash(), size, extension);
		}
	};

	public String description() {
		return data.getString("description");
	}
}
