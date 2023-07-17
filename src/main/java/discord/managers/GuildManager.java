package discord.managers;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import discord.client.DiscordClient;
import discord.resources.Guild;
import discord.util.Util;
import sj.SjObject;

public class GuildManager extends ResourceManager<Guild> {
	public GuildManager(DiscordClient client) {
		super(client);
	}

	@Override
	public Guild construct(SjObject data) {
		return new Guild(client, data);
	}

	/**
	 * https://discord.com/developers/docs/resources/user#get-current-user-guilds
	 */
	@Override
	public CompletableFuture<Guild> fetch(String id, boolean force) {
		return super.fetch(id, "/guilds/" + id, force);
	}

	/**
	 * https://discord.com/developers/docs/resources/guild#create-guild
	 */
	public CompletableFuture<Guild> create(Guild.CreatePayload payload) {
		return client.api.post("/guilds", payload.toJsonString()).thenApply(r -> cache(r.toJsonObject()));
	}

	/**
	 * https://discord.com/developers/docs/resources/guild#modify-guild
	 */
	public CompletableFuture<Guild> edit(String id, Guild.EditPayload payload) {
		return client.api.patch("/guilds/" + id, payload.toJsonString()).thenApply(r -> cache(r.toJsonObject()));
	}

	/**
	 * https://discord.com/developers/docs/resources/guild#delete-guild
	 */
	public CompletableFuture<Void> delete(String id) {
		return client.api.delete("/guilds/" + id).thenRun(Util.NO_OP);
	}

	/**
	 * https://discord.com/developers/docs/resources/user#get-current-user-guilds
	 * Clear the guild cache, fetches partial guilds from {@code /users/@me/guilds},
	 * then fetches each full guild using the ids from each partial guild.
	 * 
	 * @param consumer Consumes {@code Guild}s as they are fetched from Discord
	 */
	public CompletableFuture<Void> refresh(Consumer<Guild> consumer) {
		return _refresh(o -> fetch(o.getString("id"), true).thenAccept(consumer::accept));
	}

	/**
	 * https://discord.com/developers/docs/resources/user#get-current-user-guilds
	 * Clear the guild cache, fetches partial guilds from {@code /users/@me/guilds},
	 * then fetches each full guild using the ids from each partial guild.
	 */
	public CompletableFuture<Void> refresh() {
		return _refresh(o -> fetch(o.getString("id"), true));
	}

	private CompletableFuture<Void> _refresh(Function<SjObject, CompletableFuture<?>> mapper) {
		return client.api.get("/users/@me/guilds")
			.thenCompose(r -> {
				cache.clear();
				final var arr = r.toJsonObjectArray().stream()
					.map(mapper)
					.toArray();
				return CompletableFuture.allOf(Arrays.copyOf(arr, arr.length, CompletableFuture[].class));
			});
	}
}
