package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.channels.Channel;
import discord.util.Util;
import simple_json.SjObject;

public class ChannelManager extends ResourceManager<Channel> {
	public ChannelManager(final DiscordClient client) {
		super(client);
	}

	@Override
	public Channel construct(final SjObject data) {
		return Channel.fromJSON(client, data);
	}

	// edit group dm channels

	public CompletableFuture<Void> delete(final String id) {
		return client.api.delete("/channels/" + id).thenRunAsync(Util.DO_NOTHING);
	}

	@Override
	public CompletableFuture<Channel> fetch(final String id, final boolean force) {
		return super.fetch(id, "/channels/" + id, force);
	}

	public CompletableFuture<Void> fetchDMs() {
		return client.api.get("/users/@me/channels")
				.thenAcceptAsync((final var r) -> r.toJSONObjectArray().forEach(this::cache));
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		throw new UnsupportedOperationException("Global channels cache cannot be refreshed");
	}
}
