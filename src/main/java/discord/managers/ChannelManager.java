package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.enums.ChannelType;
import discord.structures.channels.CategoryChannel;
import discord.structures.channels.Channel;
import discord.structures.channels.DMBasedChannel;
import discord.structures.channels.DMChannel;
import discord.structures.channels.GroupDMChannel;
import discord.structures.channels.TextChannel;
import discord.util.DiscordResourceMap;
import simple_json.JSON;
import simple_json.JSONObject;

public class ChannelManager extends DataManager<Channel> {

	public Channel createCorrectChannel(JSONObject data) {
		final var type = ChannelType.resolve(data.getLong("type"));
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
	public Channel cache(JSONObject data) {
		return cache(createCorrectChannel(data));
	}

	public CompletableFuture<Channel> edit(String id, Channel.Payload payload) {
		final var channelData = payload.toString();
		return CompletableFuture.supplyAsync(() -> {
			final var responseData = JSON.parseObject(client.api.patch("/channels/" + id, channelData));
			return createCorrectChannel(responseData);
		});
	}

	public CompletableFuture<Void> delete(String id) {
		return CompletableFuture.runAsync(() -> client.api.delete("/channels/" + id));
	}

	@Override
	public Channel fetch(String id, boolean force) {
		return super.fetch(id, "/channels/" + id, force);
	}

	public DiscordResourceMap<DMBasedChannel> fetchDMs() {
		final var channels = new DiscordResourceMap<DMBasedChannel>();

		for (final var rawChannel : JSON.parseObjectArray(client.api.get("/users/@me/channels"))) {
			final var channel = (DMBasedChannel) cache((JSONObject) rawChannel);
			channels.put(channel);
		}

		return channels;
	}

}
