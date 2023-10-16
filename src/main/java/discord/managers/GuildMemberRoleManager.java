package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.GuildMember;
import discord.resources.Role;
import discord.util.Util;
import sj.SjObject;

public class GuildMemberRoleManager extends GuildResourceManager<Role> {
	private final GuildMember member;

	public GuildMemberRoleManager(DiscordClient client, GuildMember member) {
		super(client, member.getGuildAsync().join(), null);
		this.member = member;
	}

	@Override
	public Role construct(SjObject data) {
		return new Role(client, data, guild);
	}

	/**
	 * This method always throws an {@code UnsupportedOperationException}. The Discord API
	 * does not have an endpoint for getting all roles of an individual user. They must be
	 * acquired from guild member objects from the "Get Guild Member" endpoint.
	 */
	@Override
	public CompletableFuture<Role> get(String id, boolean force) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Roles can only be fetched individually");
	}

	public CompletableFuture<Void> add(String id) {
		return client.api.put(pathWithId(id), null).thenAccept(r -> cache(r.asObject()));
	}

	public CompletableFuture<Void> remove(String id) {
		return client.api.delete(pathWithId(id)).thenRun(() -> cache.remove(id));
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		return guild.roles.refreshCache().thenRun(() -> {
			final var freshIds = member.getData().getStringArray("roles");
			final var deletedIds = Util.setDifference(cache.keySet(), freshIds);
			deletedIds.forEach(cache::remove);
			freshIds.forEach(id -> guild.roles.get(id).thenAccept(this::cache));
		});
	}
}
