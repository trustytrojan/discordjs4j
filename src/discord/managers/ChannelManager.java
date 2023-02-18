package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.util.BetterMap;
import discord.client.DiscordClient;
import discord.enums.ChannelType;
import discord.structures.channels.CategoryChannel;
import discord.structures.channels.Channel;
import discord.structures.channels.DMChannel;
import discord.structures.channels.GroupDMChannel;
import discord.structures.channels.TextBasedChannel;
import discord.structures.channels.TextChannel;
import discord.util.JSON;

public class ChannelManager extends DataManager<Channel> {

	public Channel createCorrectChannel(BetterJSONObject data) {
		final var type = ChannelType.get(data.getLong("type"));
		return switch(type) {
			case GuildText -> new TextChannel(client, data);
			case DM -> new DMChannel(client, data);
			case GroupDM -> new GroupDMChannel(client, data);
			case GuildCategory -> new CategoryChannel(client, data);
			// ...
			default -> null;
		};
	}

	public ChannelManager(DiscordClient client) {
		super(client);
	}

	@Override
	public Channel forceCache(BetterJSONObject data) {
		return cache(createCorrectChannel(data));
	}

	@Override
	public CompletableFuture<Channel> fetch(String id, boolean force) {
		final var path = String.format("/channels/%s", id);
		return super.fetch(id, path, force);
	}

	public CompletableFuture<BetterMap<String, TextBasedChannel>> fetchDMs() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				final var path = "/users/@me/channels";
				final var raw_dms = JSON.parseObjectArray(client.api.get(path));
				final var dms = new BetterMap<String, TextBasedChannel>();
				for(final var raw_dm : raw_dms) {
					final var dm = (TextBasedChannel)cache(raw_dm);
					dms.put(dm.id(), dm);
				}
				return dms;
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

}
