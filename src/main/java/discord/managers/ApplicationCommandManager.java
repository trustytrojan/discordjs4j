package discord.managers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONArray;

import discord.client.BotDiscordClient;
import discord.structures.ApplicationCommand;
import discord.util.IdMap;
import simple_json.JSONObject;

public class ApplicationCommandManager extends ResourceManager<ApplicationCommand> {
	private final BotDiscordClient client;
	private final String basePath;

	public ApplicationCommandManager(final BotDiscordClient client) {
		super(client);
		this.client = client;
		basePath = "/applications/" + client.application.id() + "/commands";
	}

	@Override
	public ApplicationCommand construct(final JSONObject data) {
		return new ApplicationCommand(client, data);
	}

	@Override
	public CompletableFuture<ApplicationCommand> fetch(final String id, final boolean force) {
		return super.fetch(id, basePath + '/' + id, force);
	}

	public CompletableFuture<ApplicationCommand> create(final ApplicationCommand.Payload payload) {
		return client.api.post(basePath, payload.toJSONString())
			.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<ApplicationCommand> edit(final String id, final ApplicationCommand.Payload payload) {
		return client.api.patch(basePath + '/' + id, payload.toJSONString())
			.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<Void> delete(final String id) {
		return client.api.delete(basePath + '/' + id).thenRunAsync(() -> cache.remove(id));
	}

	public CompletableFuture<IdMap<ApplicationCommand>> set(final List<ApplicationCommand.Payload> commandPayloads) {
		cache.clear();
		final var dataToSend = JSONArray.toJSONString(commandPayloads);
		final var commands = new IdMap<ApplicationCommand>();
		return client.api.put(basePath, dataToSend).thenApplyAsync((final var r) -> {
			r.toJSONObjectArray().forEach((final var o) -> commands.put(cache(o)));
			return commands;
		});
	}

	public CompletableFuture<Void> refreshCache() {
		cache.clear();
		return client.api.get(basePath)
			.thenAcceptAsync((final var r) -> r.toJSONObjectArray().forEach(this::cache));
	}
}
