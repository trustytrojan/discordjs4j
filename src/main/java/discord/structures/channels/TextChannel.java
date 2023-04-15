package discord.structures.channels;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.Guild;
import simple_json.JSONObject;

public class TextChannel implements GuildTextBasedChannel {

	private final DiscordClient client;
	private JSONObject data;

	public final MessageManager messages;

	public final Guild guild;

	public TextChannel(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
		messages = new MessageManager(client, this);
		guild = client.guilds.fetch(guildId()).join();
	}

	public String topic() {
		return data.getString("topic");
	}

	public Long slowmodeDuration() {
		return data.getLong("rate_limit_per_user");
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

	@Override
	public MessageManager messages() {
		return messages;
	}

	@Override
	public Guild guild() {
		return guild;
	}

}
