package discord.structures.channels;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.Guild;
import simple_json.JSONObject;

public class TextChannel implements GuildChannel, TextBasedChannel {

	private final DiscordClient client;
	private JSONObject data;

	private final MessageManager messages;

	public final Guild guild;

	public TextChannel(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
		messages = new MessageManager(client, this);
		guild = client.guilds.fetch(guildId());
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
	public DiscordClient client() {
		return client;
	}

	@Override
	public MessageManager messages() {
		return messages;
	}

}
