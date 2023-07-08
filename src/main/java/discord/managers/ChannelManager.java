package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.channels.Channel;
import discord.structures.channels.GroupDMChannel;
import discord.structures.channels.GuildChannel;
import sj.SjObject;

public class ChannelManager extends ResourceManager<Channel> {
	public ChannelManager(DiscordClient client) {
		super(client);
	}

	@Override
	public Channel construct(SjObject data) {
		return Channel.fromJSON(client, data);
	}

	public CompletableFuture<GroupDMChannel> editGroupDM(String id, GroupDMChannel.Payload payload) {
		return client.api.patch("/channels/" + id, payload.toJsonString())
			.thenApply(r -> (GroupDMChannel) cache(r.toJsonObject()));
	}

	public CompletableFuture<GuildChannel> editGuildChannel(String id, GuildChannel.Payload payload) {
		return client.api.patch("/channels/" + id, payload.toJsonString())
			.thenApply(r -> (GuildChannel) cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete("/channels/" + id).thenAccept(r -> {
			cache.remove(id);
			final var guildId = r.toJsonObject().getString("guild_id");
			if (guildId != null) {
				client.guilds.fetch(guildId).thenAccept(g -> g.channels.cache.remove(id));
			}
		});
	}

	@Override
	public CompletableFuture<Channel> fetch(String id, boolean force) {
		return super.fetch(id, "/channels/" + id, force);
	}

	public CompletableFuture<Void> fetchDMs() {
		return client.api.get("/users/@me/channels")
				.thenAcceptAsync(r -> r.toJsonObjectArray().forEach(this::cache));
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		throw new UnsupportedOperationException("Global channels cache cannot be refreshed");
	}
}
