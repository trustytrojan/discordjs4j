package discord.structures;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONAware;

import discord.client.DiscordClient;
import discord.structures.channels.TextBasedChannel;
import discord.structures.components.ActionRow;
import discord.structures.components.MessageComponent;
import simple_json.JSONObject;

public class Message implements DiscordResource {
	private final DiscordClient client;
	private JSONObject data;

	public List<MessageComponent> components;

	public final User author;
	public final TextBasedChannel channel;

	public Message(final DiscordClient client, final JSONObject data) {
		this.client = client;
		final var authorFuture = client.users.fetch(data.getObject("author").getString("id"));
		final var channelFuture = client.channels.fetch(data.getString("channel_id"));
		CompletableFuture.allOf(authorFuture, channelFuture);
		author = authorFuture.join();
		channel = (TextBasedChannel) channelFuture.join();
		setData(data);
	}

	public String content() {
		return data.getString("content");
	}

	public Boolean isPinned() {
		return data.getBoolean("pinned");
	}

	@Override
	public String apiPath() {
		return "/channels/" + channel.id() + "/messages/" + id();
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public void setData(final JSONObject data) {
		this.data = data;
		final var rawComponents = data.getObjectArray("components");
		if (rawComponents != null) {
			final var _components = new LinkedList<MessageComponent>();
			for (final var rawComponent : data.getObjectArray("components")) {
				_components.add(MessageComponent.construct(rawComponent));
			}
			components = Collections.unmodifiableList(_components);
		} else {
			components = Collections.emptyList();
		}
	}

	public static class Payload implements JSONAware {
		public String content;
		public String replyMessageId;
		public List<Embed> embeds;
		public List<ActionRow> components;
		//public List<Attachment> attachments;

		public JSONObject toJSONObject() {
			final var obj = new JSONObject();

			if (content != null) {
				obj.put("content", content);
			}

			if (replyMessageId != null) {
				final var messageReference = new JSONObject();
				messageReference.put("message_id", replyMessageId);
				obj.put("message_reference", messageReference);
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
		public String toJSONString() {
			return toJSONObject().toString();
		}
	}
}
