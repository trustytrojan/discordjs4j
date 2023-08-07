package discord.resources;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.channels.GuildChannel;
import discord.resources.channels.MessageChannel;
import discord.resources.guilds.Guild;
import discord.structures.components.ActionRow;
import discord.structures.components.MessageComponent;
import sj.SjObject;
import sj.SjSerializable;

public class Message extends AbstractDiscordResource {
	public static class Payload implements SjSerializable {
		public String content;
		public String replyMessageId;
		public List<Embed> embeds;
		public List<ActionRow> components;
		//public List<Attachment> attachments;

		public SjObject toJsonObject() {
			final var obj = new SjObject();
			if (content != null)
				obj.put("content", content);

			if (replyMessageId != null) {
				obj.put("message_reference", "{\"message_id\":\"" + replyMessageId + "\"}");
			}

			if (embeds != null && embeds.size() > 0) {
				obj.put("embeds", embeds);
			}

			if (components != null && components.size() > 0) {
				obj.put("components", components);
			}

			// if (attachments != null && attachments.size() > 0) {
			// 	obj.put("attachments", attachments);
			// }

			return obj;
		}

		@Override
		public String toJsonString() {
			return toJsonObject().toString();
		}
	}

	private List<MessageComponent> components;
	public final String channelId;
	public final String authorId;

	public Message(DiscordClient client, SjObject data) {
		super(client, data);
		channelId = data.getString("channel_id");
		authorId = data.getObject("author").getString("id");
	}

	public CompletableFuture<MessageChannel> getChannelAsync() {
		return client.channels.get(channelId).thenApply(c -> (MessageChannel) c);
	}

	public MessageChannel getChannel() {
		return getChannelAsync().join();
	}

	public CompletableFuture<User> getAuthorAsync() {
		return client.users.get(authorId);
	}

	public User getAuthor() {
		return getAuthorAsync().join();
	}

	public CompletableFuture<Guild> getGuildAsync() {
		return getChannelAsync().thenApply(c -> ((GuildChannel) c).getGuild());
	}

	public Guild getGuild() {
		return getGuildAsync().join();
	}

	public String getContent() {
		return data.getString("content");
	}

	public Boolean isPinned() {
		return data.getBoolean("pinned");
	}

	public List<MessageComponent> getComponents() {
		return Collections.unmodifiableList(components);
	}

	@Override
	public void setData(SjObject data) {
		this.data = data;
		final var rawComponents = data.getObjectArray("components");
		if (rawComponents != null) {
			final var _components = new LinkedList<MessageComponent>();
			for (final var rawComponent : data.getObjectArray("components"))
				_components.add(MessageComponent.construct(rawComponent));
			components = Collections.unmodifiableList(_components);
		} else {
			components = Collections.emptyList();
		}
	}
}
