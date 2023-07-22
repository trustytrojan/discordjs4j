package discord.managers.guild;

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
	public Role construct(SjObject data) {
		return new Role(client, data);
	}

	@Override
	public CompletableFuture<Role> get(String id, boolean force) {
		throw new UnsupportedOperationException("Roles cannot be fetched individually");
	}

	public CompletableFuture<Role> create(Role.Payload payload) {
		return client.api.post(basePath, payload.toJsonString()).thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Role> edit(String id, Role.Payload payload) {
		return client.api.patch(pathWithId(id), payload.toJsonString()).thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(pathWithId(id)).thenRun(() -> cache.remove(id));
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath).thenAccept(r -> r.toJsonObjectArray().forEach(this::cache));
	}
}
