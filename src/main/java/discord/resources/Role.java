package discord.resources;

import discord.client.DiscordClient;
import discord.enums.Permission;
import discord.resources.guilds.Guild;
import discord.util.BitFlagSet;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.URLFactory;
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
				obj.put("mentionable", Boolean.TRUE);
			return obj.toJsonString();
		}
	}

	public Role(DiscordClient client, Guild guild, SjObject data) {
		super(client, guild, data, "/roles");
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}

	public BitFlagSet<Permission> permissions() {
		return new BitFlagSet<>(data.getLong("permissions"));
	}

	public Long color() {
		return data.getLong("color");
	}

	public Long position() {
		return data.getLong("position");
	}

	public Boolean hoist() {
		return data.getBoolean("hoist");
	}

	public Boolean managed() {
		return data.getBoolean("managed");
	}

	public Boolean mentionable() {
		return data.getBoolean("mentionable");
	}

	public final URLFactory icon = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("icon");
		}

		@Override
		public String url(AllowedSize size, AllowedExtension extension) {
			return CDN.roleIcon(id, hash(), size, extension);
		}
	};

	public String unicodeEmoji() {
		return data.getString("unicode_emoji");
	}
}
