package discord.structures.channels;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.Guild;
import simple_json.SjObject;

public class TextChannel implements GuildChannel, TextBasedChannel {
	private final DiscordClient client;
	private SjObject data;

	private final MessageManager messages;
	private final Guild guild;

	public TextChannel(final DiscordClient client, final SjObject data) {
		this.client = client;
		this.data = data;
		guild = client.guilds.fetch(data.getString("guild_id")).join();
		messages = new MessageManager(client, this);
	}

	public CompletableFuture<GuildChannel> edit(final Payload payload) {
		return guild.channels.edit(id(), payload);
	}

	public String topic() {
		return data.getString("topic");
	}

	public Long slowmodeDuration() {
		return data.getLong("rate_limit_per_user");
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
	public DiscordClient client() {
		return client;
	}

	@Override
	public MessageManager messages() {
		return messages;
	}
	
	@Override
	public Guild guild() {
		return guild;
	}

	public static class Payload extends GuildChannel.Payload {
		public Channel.Type type;
		public String topic;
		public boolean nsfw;
		public Short rateLimitPerUser;
		public String parentId;

		@Override
		public String toJSONString() {
			final var obj = toJSONObject();

			if (type != null) {
				obj.put("type", type.value);
			}

			if (topic != null) {
				obj.put("topic", topic);
			}

			if (nsfw) {
				obj.put("nsfw", Boolean.TRUE);
			}

			if (rateLimitPerUser != null) {
				obj.put("rate_limit_per_user", rateLimitPerUser);
			}

			if (parentId != null) {
				obj.put("parent_id", parentId);
			}

			return obj.toString();
		}
	}
}
