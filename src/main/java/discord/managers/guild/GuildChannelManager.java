package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.channels.GuildChannel;
import discord.util.Util;
import simple_json.SjObject;

public class GuildChannelManager extends GuildResourceManager<GuildChannel> {
	public GuildChannelManager(final DiscordClient client, final Guild guild) {
		super(client, guild);
		refreshCache();
	}

	@Override
	public GuildChannel construct(final SjObject data) {
		return (GuildChannel) client.channels.construct(data);
	}

	@Override
	public CompletableFuture<GuildChannel> fetch(final String id, final boolean force) {
		return super.fetch(id, "/channels/" + id, force)
			.thenApplyAsync((final var channel) -> (GuildChannel) client.channels.cache(channel));
	}

	public CompletableFuture<GuildChannel> create(final GuildChannel.Payload payload) {
		return client.api.post("/guilds/" + guild.id() + "/channels", payload.toJSONString())
			.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<GuildChannel> edit(final String id, final GuildChannel.Payload payload) {
		return client.api.patch("/channels/" + id, payload.toJSONString())
			.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<Void> delete(final String id) {
		return client.api.delete("/channels/" + id).thenRunAsync(Util.DO_NOTHING);
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		return client.api.get("/guilds/" + guild.id() + "/channels")
			.thenAcceptAsync((final var r) -> {
				for (final var rawChannel : r.toJSONObjectArray()) {
					client.channels.cache(cache(rawChannel));
				}
			});
	}
}
