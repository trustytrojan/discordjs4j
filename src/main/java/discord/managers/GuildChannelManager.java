package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.channels.GuildChannel;
import discord.util.IdMap;
import simple_json.JSON;
import simple_json.JSONObject;

public class GuildChannelManager extends DataManager<GuildChannel> {
	
	public final Guild guild;

	public GuildChannelManager(DiscordClient client, Guild guild) {
		super(client);
		this.guild = guild;
	}

	@Override
	public GuildChannel construct(JSONObject data) {
		return (GuildChannel) client.channels.createCorrectChannel(data);
	}

	@Override
	public GuildChannel fetch(String id, boolean force) {
		return super.fetch(id, "/channels/" + id, force);
	}

	public CompletableFuture<GuildChannel> edit(String id, GuildChannel.Payload payload) {
		return CompletableFuture.supplyAsync(() -> {
			final var updatedChannelData = JSON.parseObject(client.api.patch("/channels/" + id, payload.toString()));
			return (GuildChannel) client.channels.createCorrectChannel(updatedChannelData);
		});
	}

	public IdMap<GuildChannel> fetch() {
		final var path = "/guilds/" + guild.id() + "/channels";
		final var channels = new IdMap<GuildChannel>();

		for (final var channelData : JSON.parseObjectArray(client.api.get(path))) {
			channels.put((GuildChannel) cache(channelData));
		}

		return channels;
	}

}
