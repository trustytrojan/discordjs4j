package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Guild;
import simple_json.SjObject;

public class GuildManager extends ResourceManager<Guild> {
	public GuildManager(final DiscordClient client) {
		super(client);
	}

	@Override
	public Guild construct(final SjObject data) {
		return new Guild(client, data);
	}

	@Override
	public CompletableFuture<Guild> fetch(final String id, final boolean force) {
		return super.fetch(id, "/guilds/" + id, force);
	}

	public CompletableFuture<Void> refreshCache() {
		return client.api.get("/users/@me/guilds").thenAcceptAsync((final var r) ->
			r.toJSONObjectArray().parallelStream()
				.map((final var partial) -> partial.getString("id"))
				.forEach(this::fetch)
		);
	}
}
