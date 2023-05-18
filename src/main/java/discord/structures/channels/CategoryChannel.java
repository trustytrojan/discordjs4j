package discord.structures.channels;

import discord.client.DiscordClient;
import simple_json.SjObject;

public class CategoryChannel extends AbstractGuildChannel {
	public static class Payload extends GuildChannel.Payload {
		public Payload(String name) {
			super(name);
		}

		@Override
		public String toJSONString() {
			final var obj = toSjObject();
			obj.put("type", Channel.Type.GUILD_CATEGORY.value);
			return obj.toJSONString();
		}
	}

	public CategoryChannel(DiscordClient client, SjObject data) {
		super(client, data);
	}

	// public List<NonCategoryChannel> children() { ... }
}
