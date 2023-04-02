package discord.structures;

import java.util.Collections;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

import discord.client.DiscordClient;
import discord.structures.channels.TextBasedChannel;
import simple_json.JSONObject;

public class Message implements DiscordResource {

	private final DiscordClient client;
	private final JSONObject data;
	
	public final User author;
	public final TextBasedChannel channel;

	public Message(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
		author = client.users.fetch(authorId());
		channel = (TextBasedChannel) client.channels.fetch(channelId());
	}

	public String channelId() {
		return data.getString("channel_id");
	}

	public String authorId() {
		return data.getObject("author").getString("id");
	}

	public String content() {
		return data.getString("content");
	}

	public Boolean isTTS() {
		return data.getBoolean("tts");
	}

	public Boolean mentionsEveryone() {
		return data.getBoolean("mention_everyone");
	}

	public Boolean isPinned() {
		return data.getBoolean("pinned");
	}

	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	public static class Payload implements JSONAware {
		private String content;
		private String reply_to;
		private LinkedList<Embed> embeds = new LinkedList<>();
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
				obj.put("embeds", (JSONArray) embeds);
			}

			return obj;
		}

		@Override
		public String toJSONString() {
			return toJSONObject().toString();
		}
	}

}
