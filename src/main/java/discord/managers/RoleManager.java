package discord.managers;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.Role;
import discord.resources.guilds.Guild;
import sj.SjObject;

public class RoleManager extends GuildResourceManager<Role> {
	public RoleManager(final DiscordClient client, final Guild guild) {
		super(client, guild, "/roles");
	}

	@Override
	public Role construct(final SjObject data) {
		return new Role(client, data, guild);
	}

	@Override
	public CompletableFuture<Role> get(final String id, final boolean force) {
		Objects.requireNonNull(id);
		if (!force) {
			final var cached = cache.get(id);
			if (cached != null)
				return CompletableFuture.completedFuture(cached);
		}
		return refreshCache().thenApply(__ -> cache.get(id));
	}

	public CompletableFuture<Role> create(final Role.Payload payload) {
		return client.api.post(basePath, payload.toJsonString()).thenApply(r -> cache(r.asObject()));
	}

	public CompletableFuture<Role> edit(final String id, final Role.Payload payload) {
		return client.api.patch(pathWithId(id), payload.toJsonString()).thenApply(r -> cache(r.asObject()));
	}

	public CompletableFuture<Void> delete(final String id) {
		return client.api.delete(pathWithId(id)).thenRun(() -> cache.remove(id));
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath).thenAccept(this::cacheNewDeleteOld);
	}
}
