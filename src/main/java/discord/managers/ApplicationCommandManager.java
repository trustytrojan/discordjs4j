package discord.managers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONArray;

import discord.client.DiscordClient;
import discord.structures.ApplicationCommand;
import discord.util.IdMap;
import simple_json.JSONObject;

public class ApplicationCommandManager extends DataManager<ApplicationCommand> {

	private final DiscordClient.Bot client;

	public ApplicationCommandManager(DiscordClient.Bot client) {
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
	public ApplicationCommand fetch(String id, boolean force) {
		return super.fetch(id, commandsPath(id), force);
	}

	public CompletableFuture<ApplicationCommand> create(ApplicationCommand.Payload payload) {
		return CompletableFuture.supplyAsync(() -> {
			final var data = client.api.post(commandsPath(), payload.toString()).toJsonObject();
			return cache(data);
		});
	}

	public CompletableFuture<IdMap<ApplicationCommand>> set(List<ApplicationCommand.Payload> commands) {
		final var dataToSend = JSONArray.toJSONString(commands);

		return CompletableFuture.supplyAsync(() -> {
			final var responseData = client.api.put(commandsPath(), dataToSend).toJsonObjectArray();
			final var commandsSet = new IdMap<ApplicationCommand>();

			for (final var commandData : responseData)
				commandsSet.put(cache(commandData));

			return commandsSet;
		});
	}

	public CompletableFuture<Void> delete(String id) {
		return CompletableFuture.runAsync(() -> {
			client.api.delete(commandsPath(id));
			cache.remove(id);
		});
	}

	public CompletableFuture<Void> refresh() {
		cache.clear();
		return CompletableFuture.runAsync(() -> {
			for (final var obj : client.api.get(commandsPath()).toJsonObjectArray())
				cache(obj);
		});
	}

}
