package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.GuildMember;
import discord.resources.Role;
import sj.SjObject;

public class GuildMemberRoleManager extends GuildResourceManager<Role> {
	public final GuildMember member;
	private final String basePath;

	public GuildMemberRoleManager(final DiscordClient client, final GuildMember member) {
		super(client, member.guild);
		this.member = member;
		basePath = "/guilds/" + member.guild.id() + "/members/" + member.user.id() + "/roles/";
	}

	@Override
	public Role construct(final SjObject data) {
		return new Role(client, member.guild, data);
	}

	@Override
	public CompletableFuture<Role> fetch(String id, boolean force) {
		throw new UnsupportedOperationException("Member roles cannot be fetched individually");
	}

	public CompletableFuture<Void> add(String id) {
		return client.api.put(basePath + "/roles/" + id, null)
			.thenAccept(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> remove(final String id) {
		return client.api.delete(basePath + id).thenRun(() -> cache.remove(id));
	}

	public CompletableFuture<Void> refreshCache() {
		cache.clear();
		return CompletableFuture.allOf(member.fetch(), guild.roles.refreshCache())
			.thenRun(() -> {
				for (final var roleId : member.getData().getStringArray("roles")) {
					final var role = guild.roles.cache.get(roleId);
					if (role != null) cache(role);
				}
			});
	}
}
