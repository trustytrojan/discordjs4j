package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.channels.GuildChannel;
import discord.util.Util;
import simple_json.JSONObject;

public class GuildChannelManager extends GuildResourceManager<GuildChannel> {
	public GuildChannelManager(final DiscordClient client, final Guild guild) {
		super(client, guild);
		refreshCache();
	}

	@Override
	public GuildChannel construct(final JSONObject data) {
		return (GuildChannel) client.channels.construct(data);
	}

	@Override
	public CompletableFuture<GuildChannel> fetch(final String id, final boolean force) {
		return super.fetch(id, "/channels/" + id, force);
	}

	public CompletableFuture<GuildChannel> create(final GuildChannel.Payload payload) {
		return client.api.post("/guilds/" + guild.id() + "/channels", payload.toJSONString())
			.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<GuildChannel> edit(final String id, final GuildChannel.Payload payload) {
		return client.api.patch("/channels/" + id, payload.toJSONString())
			.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete("/channels/" + id).thenRunAsync(Util.DO_NOTHING);
	}

	public CompletableFuture<Void> refreshCache() {
		return client.api.get("/guilds/" + guild.id() + "/channels")
			.thenAcceptAsync((final var r) -> r.toJSONObjectArray().forEach(this::cache));
	}
}
