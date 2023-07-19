package discord.resources;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import discord.client.DiscordClient;
import discord.resources.channels.GuildChannel;
import discord.resources.channels.MessageChannel;
import discord.resources.components.ActionRow;
import discord.resources.components.MessageComponent;
import sj.SjObject;
import sj.SjSerializable;

public class Message extends AbstractDiscordResource {
	public static class Payload implements SjSerializable {
		public String content;
		public String replyMessageId;
		public List<Embed> embeds;
		public List<ActionRow> components;
		//public List<Attachment> attachments;

		public SjObject toJSONObject() {
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
			return toJSONObject().toString();
		}
	}

	public List<MessageComponent> components;
	public final User author;
	public final MessageChannel channel;

	private final boolean inGuild;
	private final String url;

	public Message(DiscordClient client, MessageChannel channel, SjObject data) {
		super(client, data, "/channels/" + channel.id() + "/messages");
		this.channel = channel;
		author = client.users.get(data.getObject("author").getString("id")).join();
		inGuild = (channel instanceof GuildChannel);
		final var guildId = (inGuild)
				? ((GuildChannel) channel).guildId()
				: "@me";
		url = "https://discord.com/channels/" + guildId + "/" + channel.id() + "/" + id;
	}

	public String content() {
		return data.getString("content");
	}

	public Boolean isPinned() {
		return data.getBoolean("pinned");
	}

	public String url() {
		return url;
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
