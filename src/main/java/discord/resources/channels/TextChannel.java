package discord.resources.channels;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import sj.SjObject;

public class TextChannel extends AbstractGuildChannel implements MessageChannel {
	public static class Payload extends GuildChannel.Payload {
		public Channel.Type type;
		public String topic;
		public boolean nsfw;
		public Short rateLimitPerUser;
		public String parentId;

		public Payload(String name) {
			super(name);
		}

		@Override
		public String toJsonString() {
			final var obj = toSjObject();
			if (type != null)
				obj.put("type", type.value);
			if (topic != null)
				obj.put("topic", topic);
			if (nsfw)
				obj.put("nsfw", Boolean.TRUE);
			if (rateLimitPerUser != null)
				obj.put("rate_limit_per_user", rateLimitPerUser);
			if (parentId != null)
				obj.put("parent_id", parentId);
			return obj.toString();
		}
	}

	private final MessageManager messages;

	public TextChannel(DiscordClient client, SjObject data) {
		super(client, data);
		messages = new MessageManager(client, this);
	}

	public String topic() {
		return data.getString("topic");
	}

	public Long slowmodeDuration() {
		return data.getLong("rate_limit_per_user");
	}

	public boolean nsfw() {
		return data.getBooleanDefaultFalse("nsfw");
	}

	@Override
	public MessageManager getMessageManager() {
		return messages;
	}
}
