package discord.managers.guild;

import java.util.List;
import java.util.Objects;
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
		final var id = data.getObject("user").getString("id");
		final var cached = cache.get(id);
		if (cached == null) return cache(construct(data));
		cached.setData(data);
		return cached;
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath).thenAccept(this::cacheNewDeleteOld);
	}

	/**
	 * https://discord.com/developers/docs/resources/guild#list-guild-members
	 * <p>
	 * Does not affect the cache.
	 * @param limit max number of members to return (1-1000)
	 * @param afterId the highest user id in the previous page
	 * @return A list of guild member objects that are members of the guild.
	 */
	public CompletableFuture<List<GuildMember>> list(int limit, String afterId) {
		final var sb = new StringBuilder(basePath);
		final boolean limitProvided = (limit > 0),
					  afterIdProvided = (afterId != null);
		if (limitProvided || afterIdProvided) {
			sb.append('?');
			final Runnable appendLimitParam = () -> sb.append("limit=").append(limit),
						   appendAfterIdParam = () -> sb.append("after=").append(afterId);
			if (limitProvided && afterIdProvided) {
				appendLimitParam.run();
				sb.append('&');
				appendAfterIdParam.run();
			}
			else if (limitProvided) appendLimitParam.run();
			else if (afterIdProvided) appendAfterIdParam.run();
		}
		return client.api.get(sb.toString()).thenApply(r -> r.asObjectArray().stream().map(o -> new GuildMember(client, guild, o)).toList());
	}

	/**
	 * https://discord.com/developers/docs/resources/guild#search-guild-members
	 * <p>
	 * Does not affect the cache.
	 * @param query Query string to match username(s) and nickname(s) against.
	 * @param limit max number of members to return (1-1000)
	 * @return A list of guild member objects whose username or nickname starts with a provided string.
	 */
	public CompletableFuture<List<GuildMember>> search(String query, int limit) {
		Objects.requireNonNull(query);
		final var sb = new StringBuilder(basePath + "/search?query=" + query);
		if (limit > 0) sb.append("&limit=" + limit);
		return client.api.get(sb.toString()).thenApply(r -> r.asObjectArray().stream().map(o -> new GuildMember(client, guild, o)).toList());
	}
}
