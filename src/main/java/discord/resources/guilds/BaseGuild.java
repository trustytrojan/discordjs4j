package discord.resources.guilds;

import java.util.List;

import discord.client.DiscordClient;
import discord.resources.AbstractDiscordResource;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.URLFactory;
import sj.SjObject;

public abstract class BaseGuild extends AbstractDiscordResource {
	protected BaseGuild(DiscordClient client, SjObject data) {
		super(client, data, "/guilds");
	}

	public boolean unavailable() {
		return data.getBooleanDefaultFalse("unavailable");
	}

	public String name() {
		return data.getString("name");
	}

	public final URLFactory icon = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("icon");
		}

		@Override
		public String url(AllowedSize size, AllowedExtension extension) {
			return CDN.guildIcon(id, hash(), size, extension);
		}
	};

	public List<Guild.Feature> features() {
		return data.getStringArray("features").stream().map(s -> Guild.Feature.valueOf(s)).toList();
	}

	public Integer approximateMemberCount() {
		return data.getInteger("approximate_member_count");
	}

	public Integer approximatePresenceCount() {
		return data.getInteger("approximate_presence_count");
	}
}
