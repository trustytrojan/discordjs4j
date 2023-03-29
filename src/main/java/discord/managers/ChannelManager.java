package discord.managers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import discord.util.BetterMap;
import discord.client.DiscordClient;
import discord.enums.ChannelType;
import discord.structures.channels.CategoryChannel;
import discord.structures.channels.Channel;
import discord.structures.channels.DMChannel;
import discord.structures.channels.GroupDMChannel;
import discord.structures.channels.TextBasedChannel;
import discord.structures.channels.TextChannel;

public class ChannelManager extends DataManager<Channel> {

	public Channel createCorrectChannel(JSONObject data) {
		final var type = ChannelType.resolve(data.getIntValue("type"));
		return switch (type) {
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
	public Channel cacheNewObject(JSONObject data) {
		return cacheObject(createCorrectChannel(data));
	}

	@Override
	public Channel fetch(String id, boolean force) {
		return super.fetch(id, "/channels/" + id, force);
	}

	public BetterMap<String, TextBasedChannel> fetchDMs() {
		final var rawChannels = JSON.parseArray(client.api.get("/users/@me/channels"));
		final var channels = new BetterMap<String, TextBasedChannel>();
		for (final var rawChannel : rawChannels) {
			final var channel = (TextBasedChannel) cacheData((JSONObject) rawChannel);
			channels.put(channel.id(), channel);
		}
		return channels;
	}

}
