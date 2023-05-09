package discord.structures;

import org.json.simple.JSONAware;

import discord.client.DiscordClient;
import discord.util.CDN;
import discord.util.CDN.URLFactory;
import simple_json.SjObject;

public class Role implements GuildResource, Mentionable {
	private final DiscordClient client;
	private SjObject data;

	public final Guild guild;

	public Role(final DiscordClient client, final Guild guild, final SjObject data) {
		this.client = client;
		this.guild = guild;
		this.data = data;
	}

	@Override
	public String mention() {
		return "<@&" + id() + '>';
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
			return CDN.roleIcon(id(), hash(), size, extension);
		}
	};

	public String unicodeEmoji() {
		return data.getString("unicode_emoji");
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public Guild guild() {
		return guild;
	}

	@Override
	public SjObject getData() {
		return data;
	}

	@Override
	public void setData(final SjObject data) {
		this.data = data;
	}

	@Override
	public String apiPath() {
		return "/guilds/" + guild.id() + "/roles/" + id();
	}

	public static class Payload implements JSONAware {
		public String name;
		public String permissions;
		public Long color;
		public boolean hoist;
		public String unicodeEmoji;
		public boolean mentionable;

		@Override
		public String toJSONString() {
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
			return obj.toJSONString();
		}
	}
}
