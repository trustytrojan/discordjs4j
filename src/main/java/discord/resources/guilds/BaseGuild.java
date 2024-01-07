package discord.resources.guilds;

import java.util.List;

import discord.client.DiscordClient;
import discord.resources.AbstractDiscordResource;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.Image;
import sj.SjObject;

public class BaseGuild extends AbstractDiscordResource {
	protected BaseGuild(DiscordClient client, SjObject data) {
		super(client, data);
	}

	@Override
	public String getApiPath() {
		return "/guilds/" + getId();
	}

	public boolean isUnavailable() {
		return data.getBooleanDefaultFalse("unavailable");
	}

	public String getName() {
		return data.getString("name");
	}

	public final Image icon = new Image() {
		@Override
		public String getHash() {
			return data.getString("icon");
		}

		@Override
		public String getURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeGuildIconURL(getId(), getHash(), size, extension);
		}
	};

	public List<Guild.Feature> getFeatures() {
		return data.getStringArray("features").stream().map(s -> Guild.Feature.valueOf(s)).toList();
	}

	public Integer getApproximateMemberCount() {
		return data.getInteger("approximate_member_count");
	}

	public Integer getApproximatePresenceCount() {
		return data.getInteger("approximate_presence_count");
	}
}
