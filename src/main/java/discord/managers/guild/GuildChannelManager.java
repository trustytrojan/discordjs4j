package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.channels.GuildChannel;
import discord.util.Util;
import simple_json.SjObject;

public class GuildChannelManager extends GuildResourceManager<GuildChannel> {
	public GuildChannelManager(DiscordClient client, Guild guild) {
		super(client, guild);
	}

	@Override
	public GuildChannel construct(SjObject data) {
		return (GuildChannel) client.channels.construct(data);
	}

	@Override
	public CompletableFuture<GuildChannel> fetch(String id, boolean force) {
		return super.fetch(id, "/channels/" + id, force)
			.thenApply(channel -> (GuildChannel) client.channels.cache(channel));
	}

	public CompletableFuture<GuildChannel> create(GuildChannel.Payload payload) {
		return client.api.post("/guilds/" + guild.id() + "/channels", payload.toJSONString())
			.thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<GuildChannel> edit(String id, GuildChannel.Payload payload) {
		return client.api.patch("/channels/" + id, payload.toJSONString())
			.thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete("/channels/" + id).thenRun(Util.DO_NOTHING);
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		return client.api.get("/guilds/" + guild.id() + "/channels")
			.thenAcceptAsync(r ->
				r.toJsonObjectArray().forEach(c -> client.channels.cache(cache(c)))
			);
	}
}
