package discord.resources.guilds;

import java.util.List;

import discord.resources.Permissions;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.URLFactory;
import sj.SjObject;

public class CurrentUserGuild {
	public final String id;
	public final String name;
	private final String iconHash;
	public final boolean owner;
	public final Permissions permissions;
	public final List<String> features;
	public final int approximateMemberCount;
	public final int approximatePresenceCount;

	public final URLFactory icon = new URLFactory() {
		@Override
		public String hash() {
			return iconHash;
		}

		@Override
		public String url(AllowedSize size, AllowedExtension extension) {
			return CDN.guildIcon(id, iconHash, size, extension);
		}
	};

	public CurrentUserGuild(SjObject data) {
		id = data.getString("id");
		name = data.getString("name");
		iconHash = data.getString("icon");
		owner = data.getBoolean("owner");
		permissions = new Permissions(Long.parseLong(data.getString("permissions")));
		features = data.getStringArray("features");
		approximateMemberCount = data.getInteger("approximate_member_count");
		approximatePresenceCount = data.getInteger("approximate_presence_count");
	}
}
