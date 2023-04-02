package discord.structures.payloads;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

import discord.structures.Embed;
import simple_json.JSONObject;

public class MessagePayload implements JSONAware {

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
			obj.put("embeds", (JSONArray) embeds);
		}

		return obj;
	}

	@Override
	public String toJSONString() {
		return toJSONObject().toString();
	}

}
