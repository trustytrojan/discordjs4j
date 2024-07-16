package discord.resources.channels;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.resources.AbstractGuildResource;
import discord.resources.guilds.Guild;
import sj.SjObject;

public class TextChannel extends AbstractGuildResource implements GuildChannel, MessageChannel {
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
				obj.put("nsfw", true);
			if (rateLimitPerUser != null)
				obj.put("rate_limit_per_user", rateLimitPerUser);
			if (parentId != null)
				obj.put("parent_id", parentId);
			return obj.toString();
		}
	}

	private final MessageManager messageManager;

	TextChannel(DiscordClient client, SjObject data, Guild guild) {
		super(client, data, guild);
		messageManager = new MessageManager(client, this);
	}

	TextChannel(DiscordClient client, SjObject data) {
		super(client, data);
		messageManager = new MessageManager(client, this);
	}

	public String getTopic() {
		return data.getString("topic");
	}

	public Long getSlowmodeDuration() {
		return data.getLong("rate_limit_per_user");
	}

	public boolean isNsfw() {
		return data.getBooleanDefaultFalse("nsfw");
	}

	@Override
	public MessageManager getMessageManager() {
		return messageManager;
	}
}
