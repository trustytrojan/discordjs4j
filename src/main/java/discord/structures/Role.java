package discord.structures;

import discord.client.DiscordClient;
import discord.util.CDN;
import discord.util.CDN.URLFactory;
import sj.SjObject;
import sj.SjSerializable;

public class Role extends AbstractDiscordResource implements GuildResource, Mentionable {
	private final Guild guild;
	private final String mention = "<@&" + id + '>';
	private final String apiPath;

	public Role(DiscordClient client, Guild guild, SjObject data) {
		super(client, data);
		this.guild = guild;
		apiPath = "/guild/" + guild.id + "/roles/" + id;
	}

	@Override
	public String mention() {
		return mention;
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}

	public Permissions permissions() {
		return new Permissions(data.getLong("permissions"));
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
		public String url(final int size, final String extension) {
			return CDN.roleIcon(id, hash(), size, extension);
		}
	};

	public String unicodeEmoji() {
		return data.getString("unicode_emoji");
	}


	@Override
	public Guild guild() {
		return guild;
	}

	@Override
	public String apiPath() {
		return apiPath;
	}

	public static class Payload implements SjSerializable {
		public String name;
		public String permissions;
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
				obj.put("permissions", Boolean.TRUE);
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
}
