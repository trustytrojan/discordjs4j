package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.GuildMember;
import discord.resources.guilds.Guild;
import sj.SjObject;

public class GuildMemberManager extends GuildResourceManager<GuildMember> {
	public GuildMemberManager(DiscordClient client, Guild guild) {
		super(client, guild, "/members");
	}

	@Override
	public GuildMember construct(SjObject data) {
		return new GuildMember(client, guild, data);
	}

	@Override
	public GuildMember cache(SjObject data) {
		final var cached = cache.get(data.getObject("user").getString("id"));
		if (cached == null) return cache(construct(data));
		cached.setData(data);
		return cached;
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		cache.clear();
		return client.api.get(basePath).thenAccept(r -> r.toJsonObjectArray().forEach(this::cache));
	}
}
