package discord.structures.channels;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.Guild;

public class TextChannel implements GuildChannel, TextBasedChannel {

	private final DiscordClient client;
	public BetterJSONObject data;
	private final MessageManager messages;

	private Guild guild;
	private final CompletableFuture<Void> _guild;
	// private CategoryChannel parent;
	// private final CompletableFuture<Void> _parent;

	// data from Discord
	// public Message last_message;
	// //public ChannelFlags flags;
	// public String topic;
	// //public Long rate_limit_per_user;

	public TextChannel(DiscordClient client, BetterJSONObject data) {
		this.client = client;
		this.data = data;
		messages = new MessageManager(client, this);
		_guild = client.guilds.fetch(guild_id()).thenAccept((guild) -> this.guild = guild);
		// _parent = client.channels.fetch(parent_id()).thenAccept((channel) -> parent =
		// channel);
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
