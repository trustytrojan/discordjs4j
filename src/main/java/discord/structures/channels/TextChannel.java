package discord.structures.channels;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

	@Override
	public String toString() {
		return mention();
	}

	@Override
	public Guild guild() {
		if (guild == null) {
			try {
				_guild.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
		return guild;
	}

	public String topic() {
		return data.getString("topic");
	}

	public Long rate_limit_per_user() {
		return data.getLong("rate_limit_per_user");
	}

	@Override
	public BetterJSONObject getData() {
		return data;
	}

	@Override
	public void setData(BetterJSONObject data) {
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

}
