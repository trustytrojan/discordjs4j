package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.GuildMember;
import discord.structures.Role;
import discord.util.Util;
import simple_json.JSONObject;

public class GuildMemberRoleManager extends GuildResourceManager<Role> {
	public final GuildMember member;
	public final String basePath;

	public GuildMemberRoleManager(final DiscordClient client, final GuildMember member) {
		super(client, member.guild);
		this.member = member;
		basePath = "/guilds/" + member.guild.id() + "/members/" + member.user.id() + "/roles/";
		refreshCache();
	}

	@Override
	public Role construct(final JSONObject data) {
		return new Role(client, member.guild, data);
	}

	@Override
	public CompletableFuture<Role> fetch(String id, boolean force) {
		throw new UnsupportedOperationException("Member roles cannot be fetched individually");
	}

	public CompletableFuture<Void> add(Role.Payload payload) {
		return client.api.put(basePath, payload.toJSONString())
			.thenAcceptAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<Void> remove(String id) {
		return client.api.delete(basePath + id).thenRunAsync(Util.DO_NOTHING);
	}

	public CompletableFuture<Void> refreshCache() {
		cache.clear();
		return CompletableFuture.allOf(member.fetch(), guild.roles.refreshCache()).thenRunAsync(() -> {
			for (final var roleId : member.getData().getStringArray("roles")) {
				for (final var role : guild.roles.cache.values()) {
					if (roleId.equals(role.id())) {
						cache.put(role);
						break;
					}
				}
			}
		});
	}
}
