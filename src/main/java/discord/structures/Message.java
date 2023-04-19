package discord.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONAware;

import discord.client.DiscordClient;
import discord.structures.channels.TextBasedChannel;
import discord.structures.components.MessageComponent;
import simple_json.JSONObject;

public class Message implements DiscordResource {
	private final DiscordClient client;
	private JSONObject data;

	public final List<MessageComponent> components;

	public final User author;
	public final TextBasedChannel channel;

	public Message(final DiscordClient client, final JSONObject data) {
		this.client = client;
		this.data = data;
		author = client.users.fetch(data.getObject("author").getString("id")).join();
		channel = (TextBasedChannel) client.channels.fetch(data.getString("channel_id")).join();

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
	}

	public static class Payload implements JSONAware {
		public String content;
		public String replyToMessageId;
		public List<Embed> embeds = new ArrayList<>();
		public List<MessageComponent> components = new ArrayList<>();
		//public List<Attachment> attachments;

		public void addEmbeds(final Embed... embeds) {
			Collections.addAll(this.embeds, embeds);
		}

		public void addComponents(final MessageComponent... components) {
			Collections.addAll(this.components, components);
		}

		public JSONObject toJSONObject() {
			final var obj = new JSONObject();

			if (content != null) {
				obj.put("content", content);
			}

			if (replyToMessageId != null) {
				final var messageReference = new JSONObject();
				messageReference.put("message_id", replyToMessageId);
				obj.put("message_reference", messageReference);
			}

			if (embeds.size() > 0) {
				obj.put("embeds", embeds);
			}

			if (components.size() > 0) {
				obj.put("components", components);
			}

			return obj;
		}

		@Override
		public String toJSONString() {
			return toJSONObject().toString();
		}
	}
}
