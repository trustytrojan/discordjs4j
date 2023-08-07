package discord.resources.channels;

import discord.client.DiscordClient;
import discord.resources.AbstractGuildResource;
import sj.SjObject;

public class CategoryChannel extends AbstractGuildResource implements GuildChannel {
	public static class Payload extends GuildChannel.Payload {
		public Payload(String name) {
			super(name);
		}

		@Override
		public String toJsonString() {
			final var obj = toSjObject();
			obj.put("type", Channel.Type.GUILD_CATEGORY.value);
			return obj.toJsonString();
		}
	}

	public CategoryChannel(DiscordClient client, SjObject data) {
		super(client, data);
	}

	// public List<NonCategoryChannel> children() { ... }
}
