package discord.resources.guilds;

import discord.client.DiscordClient;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.Image;
import sj.SjObject;

public class PreviewGuild extends BaseGuild {
	public PreviewGuild(DiscordClient client, SjObject data) {
		super(client, data);
	}

	public final Image splash = new Image() {
		@Override
		public String getHash() {
			return data.getString("splash");
		}

		@Override
		public String getURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeGuildSplashURL(getId(), getHash(), size, extension);
		}
	};

	public final Image discoverySplash = new Image() {
		@Override
		public String getHash() {
			return data.getString("discovery_splash");
		}

		@Override
		public String getURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeGuildDiscoverySplashURL(getId(), getHash(), size, extension);
		}
	};

	public String getDescription() {
		return data.getString("description");
	}
}
