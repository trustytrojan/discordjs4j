package discord.managers;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
	 * 
	 * @param consumer Consumers {@code Guild} objects as they are constructed from
	 *                 fetched API data
	 */
	public CompletableFuture<Void> refresh(Consumer<Guild> consumer) {
		return client.api.get("/users/@me/guilds")
			.thenCompose(r -> {
				final var arr = r.toJsonObjectArray().stream()
					.map(o -> fetch(o.getString("id"), true).thenAccept(consumer::accept))
					.toArray();
				
				final CompletableFuture<Guild>[] cfs = new CompletableFuture[arr.length];
				for (int i = 0; i < arr.length; ++i) {
					cfs[i] = (CompletableFuture<Guild>) arr[i];
				}

				return CompletableFuture.allOf(cfs);
		});
	}

	/**
	 * ????????????????
	 * Deal with reactore core or not??????
	 */
}
