package discord.structures;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONAware;

import discord.client.DiscordClient;
import discord.structures.channels.TextBasedChannel;
import simple_json.JSONObject;

public class Message implements DiscordResource {

	private final DiscordClient client;
	private JSONObject data;
	
	public final User author;
	public final TextBasedChannel channel;

	public Message(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;

		author = client.users.fetch(data.getObject("author").getString("id")).join();
		channel = (TextBasedChannel) client.channels.fetch(data.getString("channel_id"));
	}

	public String content() {
		return data.getString("content");
	}

	public Boolean isPinned() {
		return data.getBoolean("pinned");
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
	public void setData(JSONObject data) {
		this.data = data;
	}

	public static class Payload implements JSONAware {
		private String content;
		private String reply_to;
		private List<Embed> embeds = new LinkedList<>();
		//public List<MessageComponent> components;
		//public List<Attachment> attachments;

		public void setContent(String content) {
			this.content = content;
		}

		public void setReplyTo(String message_id) {
			this.reply_to = message_id;
		}

		public void addEmbeds(Embed... embeds) {
			Collections.addAll(this.embeds, embeds);
		}

		// should I make a custom interface for this?
		public JSONObject toJSONObject() {
			final var obj = new JSONObject();

			if (content != null) {
				obj.put("content", content);
			}

			if (reply_to != null) {
				final var message_reference = new JSONObject();
				message_reference.put("message_id", reply_to);
				obj.put("message_reference", message_reference);
			}

			if (embeds.size() > 0) {
				obj.put("embeds", embeds);
			}

			return obj;
		}

		@Override
		public String toJSONString() {
			return toJSONObject().toString();
		}
	}

}
