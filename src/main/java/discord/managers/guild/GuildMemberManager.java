package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.GuildMember;
import simple_json.SjObject;

public class GuildMemberManager extends GuildResourceManager<GuildMember> {
	private final String basePath;

	public GuildMemberManager(final DiscordClient client, final Guild guild) {
		super(client, guild);
		basePath = "/guilds/" + guild.id() + "/members";
		refreshCache();
	}

	@Override
	public GuildMember construct(final SjObject data) {
		return new GuildMember(client, guild, data);
	}

	@Override
	public String getIdFromData(final SjObject data) {
		return data.getObject("user").getString("id");
	}

	@Override
	public CompletableFuture<GuildMember> fetch(final String id, final boolean force) {
		return super.fetch(id, basePath + '/' + id, force);
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath)
			.thenAcceptAsync((final var r) -> r.toJSONObjectArray().forEach(this::cache));
	}
}
