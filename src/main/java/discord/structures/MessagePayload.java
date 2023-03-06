package discord.structures;

import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONObject;

import discord.structures.embed.Embed;
import discord.util.JSON;
import discord.util.JSONable;

public class MessagePayload implements JSONable {

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
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		final var obj = new JSONObject();
		if (content != null)
			obj.put("content", content);
		if (reply_to != null)
			obj.put("message_reference", JSON.objectFrom(
				JSON.objectEntry("message_id", reply_to)
			));
		if (embeds.size() > 0)
			obj.put("embeds", JSON.buildArray(embeds));
		return obj;
	}

}
