package discord.managers;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import discord.client.DiscordClient;
import discord.resources.guilds.Guild;
import discord.util.Util;
import sj.SjObject;

public class GuildManager extends ResourceManager<Guild> {
	public GuildManager(DiscordClient client) {
		super(client, "/guilds");
	}

	@Override
	public Guild construct(SjObject data) {
		return new Guild(client, data);
	}

	public CompletableFuture<Guild> create(Guild.CreatePayload payload) {
		return client.api.post("/guilds", payload.toJsonString()).thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Guild> edit(String id, Guild.EditPayload payload) {
		return client.api.patch("/guilds/" + id, payload.toJsonString()).thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete("/guilds/" + id).thenRun(Util.NO_OP);
	}

	public CompletableFuture<Stream<Guild>> fetch() {
		return client.api.get("/users/@me/guilds").thenApply(r -> r.toJsonObjectArray().stream().map(this::cache));
	}
}
