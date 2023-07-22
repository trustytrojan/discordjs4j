package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.guilds.Guild;
import discord.util.Util;
import sj.SjObject;

public class GuildManager extends ResourceManager<Guild> {
	public GuildManager(DiscordClient client) {
		super(client, "/guilds");
	}

	@Override
	public Guild construct(SjObject data) {
		return new Guild(client, data);
	}

	public CompletableFuture<Guild> create(Guild.CreatePayload payload) {
		return client.api.post(basePath, payload.toJsonString()).thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Guild> edit(String id, Guild.EditPayload payload) {
		return client.api.patch(pathWithId(id), payload.toJsonString()).thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(pathWithId(id)).thenRun(() -> cache.remove(id));
	}

	public CompletableFuture<Void> refreshCache() {
		return client.api.get("/users/@me/guilds").thenAccept(r -> {
			final var freshIds = r.toJsonObjectArray().stream().map(this::cache).map(Guild::id).toList();
			final var deletedIds = Util.setDifference(cache.keySet(), freshIds);
			deletedIds.forEach(cache::remove);
		});
	}
}
