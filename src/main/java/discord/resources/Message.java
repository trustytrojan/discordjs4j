package discord.resources;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
				obj.put("message_reference", Map.of("message_id", replyMessageId));
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

	public Message(DiscordClient client, SjObject data) {
		super(client, data);
	}

	public CompletableFuture<Message> reply(String content) {
		final var mp = new Message.Payload();
		mp.content = content;
		return reply(mp);
	}

	public CompletableFuture<Message> reply(Embed embed) {
		final var mp = new Message.Payload();
		mp.embeds = List.of(embed);
		return reply(mp);
	}

	public CompletableFuture<Message> reply(Message.Payload payload) {
		payload.replyMessageId = getId();
		return getChannelAsync().thenCompose(c -> c.send(payload));
	}

	@Override
	public String getApiPath() {
		return "/channels/" + getChannelId() + "/messages/" + getId();
	}

	public String getChannelId() {
		return data.getString("channel_id");
	}

	public CompletableFuture<MessageChannel> getChannelAsync() {
		return client.channels.get(getChannelId()).thenApply(c -> (MessageChannel) c);
	}

	public String getAuthorId() {
		return data.getObject("author").getString("id");
	}

	public CompletableFuture<User> getAuthorAsync() {
		return client.users.get(getAuthorId());
	}

	public CompletableFuture<Guild> getGuildAsync() {
		return getChannelAsync().thenCompose(c -> ((GuildChannel) c).getGuildAsync());
	}

	public String getContent() {
		return data.getString("content");
	}

	public Boolean isPinned() {
		return data.getBoolean("pinned");
	}

	public List<MessageComponent> getComponents() {
		final var rawComponents = data.getObjectArray("components");
		if (rawComponents != null) {
			final var _components = new LinkedList<MessageComponent>();
			for (final var rawComponent : data.getObjectArray("components"))
				_components.add(MessageComponent.construct(rawComponent));
			return Collections.unmodifiableList(_components);
		} else {
			return Collections.emptyList();
		}
	}
}
