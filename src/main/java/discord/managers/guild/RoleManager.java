package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.Role;
import discord.util.Util;
import simple_json.JSONObject;

public class RoleManager extends GuildResourceManager<Role> {
	public final String basePath;

	public RoleManager(final DiscordClient client, final Guild guild) {
		super(client, guild);
		basePath = "/guilds/" + guild.id() + "/roles";
	}

	public String rolesPath(String id) {
		return basePath + '/' + id;
	}

	@Override
	public Role construct(JSONObject data) {
		return new Role(client, guild, data);
	}

	@Override
	public CompletableFuture<Role> fetch(String id, boolean force) {
		throw new UnsupportedOperationException("Roles cannot be fetched individually");
	}

	public CompletableFuture<Role> create(Role.Payload payload) {
		return client.api.post(basePath, payload.toJSONString())
			.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<Role> edit(String id, Role.Payload payload) {
		return client.api.patch(basePath + id, payload.toJSONString())
			.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(basePath + id).thenRunAsync(Util.DO_NOTHING);
	}

	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath)
			.thenAcceptAsync((final var r) -> r.toJSONObjectArray().forEach(this::cache));
	}
}
