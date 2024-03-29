package discord.resources;

import discord.client.DiscordClient;
import discord.enums.Permission;
import discord.resources.guilds.Guild;
import discord.util.BitFlagSet;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.Image;
import sj.SjObject;
import sj.SjSerializable;

public class Role extends AbstractGuildResource {
	public static class Payload implements SjSerializable {
		public String name;
		public BitFlagSet<Permission> permissions;
		public Long color;
		public boolean hoist;
		public String unicodeEmoji;
		public boolean mentionable;

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			if (name != null)
				obj.put("name", name);
			if (permissions != null)
				obj.put("permissions", permissions.toString());
			if (color != null)
				obj.put("color", color);
			if (hoist)
				obj.put("hoist", hoist);
			if (unicodeEmoji != null)
				obj.put("unicode_emoji", unicodeEmoji);
			if (mentionable)
				obj.put("mentionable", true);
			return obj.toJsonString();
		}
	}

	public Role(DiscordClient client, SjObject data,Guild guild) {
		super(client, data, guild);
	}

	@Override
	public String getApiPath() {
		throw new UnsupportedOperationException("Roles cannot be fetched individually");
	}

	public String getName() {
		return data.getString("name");
	}

	public String getDescription() {
		return data.getString("description");
	}

	public BitFlagSet<Permission> getPermissions() {
		return new BitFlagSet<>(data.getLong("permissions"));
	}

	public Long getColor() {
		return data.getLong("color");
	}

	public Long getPosition() {
		return data.getLong("position");
	}

	public Boolean isHoisted() {
		return data.getBoolean("hoist");
	}

	public Boolean isManaged() {
		return data.getBoolean("managed");
	}

	public Boolean isMentionable() {
		return data.getBoolean("mentionable");
	}

	public final Image icon = new Image() {
		@Override
		public String getHash() {
			return data.getString("icon");
		}

		@Override
		public String getURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeRoleIconURL(getId(), getHash(), size, extension);
		}
	};

	public String getUnicodeEmoji() {
		return data.getString("unicode_emoji");
	}
}
