package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.Guild;
import discord.resources.GuildMember;
import sj.SjObject;

public class GuildMemberManager extends GuildResourceManager<GuildMember> {
	private final String basePath;

	public GuildMemberManager(DiscordClient client, Guild guild) {
		super(client, guild);
		basePath = "/guilds/" + guild.id() + "/members";
	}

	@Override
	public GuildMember construct(SjObject data) {
		return new GuildMember(client, guild, data);
	}

	@Override
	public CompletableFuture<GuildMember> fetch(String id, boolean force) {
		return super.fetch(id, basePath + '/' + id, force);
	}

	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath)
			.thenAcceptAsync(r -> r.toJsonObjectArray().forEach(this::cache));
	}
}
