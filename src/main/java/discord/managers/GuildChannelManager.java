package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.util.BetterMap;
import discord.util.JSON;
import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.channels.GuildChannel;

public class GuildChannelManager extends DataManager<GuildChannel> {
	
	public final Guild guild;

	public GuildChannelManager(DiscordClient client, Guild guild) {
		super(client);
		this.guild = guild;
	}

	@Override
	public GuildChannel forceCache(BetterJSONObject data) {
		return cache((GuildChannel)client.channels.createCorrectChannel(data));
	}

	@Override
	public CompletableFuture<GuildChannel> fetch(String id, boolean force) {
		final var path = String.format("/channels/%s", id);
		return super.fetch(id, path, force);
	}

	public CompletableFuture<BetterMap<String, GuildChannel>> fetch() {
		final var path = String.format("/guilds/%s/channels", guild.id());
		return CompletableFuture.supplyAsync(() -> {
			try {
				final var raw_channels = JSON.parseObjectArray(client.api.get(path));
				final var channels = new BetterMap<String, GuildChannel>();
				for (final var raw_channel : raw_channels) {
					final var channel = (GuildChannel)cache(raw_channel);
					channels.put(channel.id(), channel);
				}
				return channels;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

}
