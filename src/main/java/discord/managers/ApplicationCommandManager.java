package discord.managers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONArray;

import discord.client.BotDiscordClient;
import discord.structures.ApplicationCommand;
import discord.util.IdMap;
import simple_json.JSONObject;

public class ApplicationCommandManager extends DataManager<ApplicationCommand> {
	private final BotDiscordClient client;

	public ApplicationCommandManager(BotDiscordClient client) {
		super(client);
		this.client = client;
	}

	@Override
	public ApplicationCommand construct(JSONObject data) {
		return new ApplicationCommand(client, data);
	}

	private String commandsPath() {
		return "/applications/" + client.application.id() + "/commands";
	}

	private String commandsPath(String id) {
		return commandsPath() + '/' + id;
	}

	@Override
	public CompletableFuture<ApplicationCommand> fetch(String id, boolean force) {
		return super.fetch(id, commandsPath(id), force);
	}

	public CompletableFuture<ApplicationCommand> create(ApplicationCommand.Payload payload) {
		return client.api.post(commandsPath(), payload.toJSONString()).thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<ApplicationCommand> edit(String id, ApplicationCommand.Payload payload) {
		return client.api.patch(commandsPath(id), payload.toJSONString()).thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(commandsPath(id)).thenRunAsync(() -> cache.remove(id));
	}

	public CompletableFuture<IdMap<ApplicationCommand>> set(List<ApplicationCommand.Payload> commands) {
		cache.clear();
		final var dataToSend = JSONArray.toJSONString(commands);
		return CompletableFuture.supplyAsync(() -> {
			final var commandsSet = new IdMap<ApplicationCommand>();
			for (final var commandData : client.api.put(commandsPath(), dataToSend).join().toJSONObjectArray())
				commandsSet.put(cache(commandData));
			return commandsSet;
		});
	}

	public CompletableFuture<Void> refreshCache() {
		cache.clear();
		return client.api.get(commandsPath()).thenAcceptAsync((final var r) -> r.toJSONObjectArray().forEach(this::cache));
	}
}
