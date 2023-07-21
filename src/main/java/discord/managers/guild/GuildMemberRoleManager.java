package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.GuildMember;
import discord.resources.Role;
import sj.SjObject;

public class GuildMemberRoleManager extends GuildResourceManager<Role> {
	public GuildMemberRoleManager(DiscordClient client, GuildMember member) {
		super(client, member.guild(), "/members" + member.id() + "/roles");
	}

	@Override
	public Role construct(SjObject data) {
		return new Role(client, data);
	}

	/**
	 * This method always throws an {@code UnsupportedOperationException}. The Discord API
	 * does not have an endpoint for getting all roles of an individual user. They must be
	 * acquired from guild member objects from the "Get Guild Member" endpoint.
	 */
	@Override
	public CompletableFuture<Role> get(String id, boolean force) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Member roles can only be fetched all at once");
	}

	public CompletableFuture<Void> add(String id) {
		return client.api.put(pathWithId(id), null).thenAccept(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> remove(String id) {
		return client.api.delete(pathWithId(id)).thenRun(() -> cache.remove(id));
	}

	// TODO: implement getAll()
}
