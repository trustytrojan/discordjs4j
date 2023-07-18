package discord.resources;

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
		public String hash() {
			return data.getString("splash");
		}

		@Override
		public String url(AllowedSize size, AllowedExtension extension) {
			return CDN.guildSplash(id, hash(), size, extension);
		}
	};

	public final URLFactory discoverySplash = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("discovery_splash");
		}

		@Override
		public String url(AllowedSize size, AllowedExtension extension) {
			return CDN.guildDiscoverySplash(id, hash(), size, extension);
		}
	};

	public String description() {
		return data.getString("description");
	}
}
