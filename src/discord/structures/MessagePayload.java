package discord.structures;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import discord.util.BetterJSONObject;
import discord.structures.embed.Embed;
import discord.util.JSON;
import discord.util.JSONable;

public class MessagePayload implements JSONable {

	private String content;
	private String reply_to;
	private ArrayList<Embed> embeds = new ArrayList<Embed>();
	//public ArrayList<MessageComponent> components;
	//public ArrayList<Attachment> attachments;

	public MessagePayload setContent(String content) {
		this.content = content;
		return this;
	}

	public MessagePayload setReplyTo(String message_id) {
		this.reply_to = message_id;
		return this;
	}

	public MessagePayload addEmbed(Embed embed) {
		this.embeds.add(embed);
		return this;
	}

	@Override
	public JSONObject toJSONObject() {
		final var obj = new BetterJSONObject();

		if(content != null) {
			obj.put("content", content);
		}

		if(reply_to != null) {
			obj.put("message_reference", JSON.buildObject(
				JSON.objectEntry("message_id", reply_to)
			));
		}

		if(embeds.size() > 0) {
			obj.put("embeds", JSON.buildArray(embeds));
		}

		return obj.innerObject;
	}

}
