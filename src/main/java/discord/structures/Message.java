package discord.structures;

import discord.client.DiscordClient;
import discord.structures.channels.TextBasedChannel;
import simple_json.JSONObject;

public class Message implements DiscordObject {

	private final DiscordClient client;
	private JSONObject data;
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
	public void setData(JSONObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

}
