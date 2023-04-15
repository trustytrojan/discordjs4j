package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.channels.GuildChannel;
import discord.util.IdMap;
import discord.util.Util;
import simple_json.JSONObject;

public class GuildChannelManager extends GuildDataManager<GuildChannel> {
	public GuildChannelManager(DiscordClient client, Guild guild) {
		super(client, guild);
	}

	@Override
	public GuildChannel construct(JSONObject data) {
		return (GuildChannel) client.channels.construct(data);
	}

	@Override
	public CompletableFuture<GuildChannel> fetch(String id, boolean force) {
		return super.fetch(id, "/channels/" + id, force);
	}

	// Sends the payload to discord, receives the updated channel, and caches it
	public CompletableFuture<GuildChannel> edit(String id, GuildChannel.Payload payload) {
		return client.api.patch("/channels/" + id, payload.toJSONString())
			.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete("/channels/" + id).thenRunAsync(Util.DO_NOTHING);
	}

	public CompletableFuture<IdMap<GuildChannel>> fetch() {
		final var path = "/guilds/" + guild.id() + "/channels";
		final var channels = new IdMap<GuildChannel>();

		return client.api.get(path).thenApplyAsync((final var r) -> {
			for (final var channelData : client.api.get(path).join().toJSONObjectArray()) {
				channels.put((GuildChannel) cache(channelData));
			}

			return channels;
		});
	}
}
