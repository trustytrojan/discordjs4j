package discord.structures;

import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

import discord.structures.embed.Embed;
import simple_json.JSONObject;

public class MessagePayload implements JSONAware {

	private String content;
	private String reply_to;
	private ArrayList<Embed> embeds = new ArrayList<Embed>();
	//public ArrayList<MessageComponent> components;
	//public ArrayList<Attachment> attachments;

	public void setContent(String content) {
		this.content = content;
	}

	public void setReplyTo(String message_id) {
		this.reply_to = message_id;
	}

	public void addEmbeds(Embed... embeds) {
		Collections.addAll(this.embeds, embeds);
	}

	@Override
	public String toJSONString() {
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

		return obj.toString();
	}

}
