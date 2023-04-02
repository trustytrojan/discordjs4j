package discord.managers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.structures.Application.Command;
import discord.client.DiscordClient;
import discord.util.DiscordResourceMap;
import simple_json.JSON;
import simple_json.JSONObject;

public class CommandManager extends DataManager<Command> {

	private final DiscordClient.Bot client;

	public CommandManager(DiscordClient.Bot client) {
		super(client);
		this.client = client;
	}

    @Override
	public Command cache(JSONObject data) {
		return cache(new Command(client, data));
	}

	private String commandsPath() {
		return "/applications/" + client.application.id() + "/commands";
	}

	private String commandsPath(String id) {
		return commandsPath() + '/' + id;
	}

	@Override
	public Command fetch(String id, boolean force) {
		return super.fetch(id, commandsPath(id), force);
	}

	public CompletableFuture<Command> create(Command.Payload payload) {
		return CompletableFuture.supplyAsync(() -> {
			final var data = JSON.parseObject(client.api.post(commandsPath(), payload.toString()));
			return cache(data);
		});
	}

	public CompletableFuture<DiscordResourceMap<Command>> set(List<Command.Payload> commands) {
		final var dataToSend = commands.toString();
		return CompletableFuture.supplyAsync(() -> {
			final var responseData = JSON.parseObjectArray(client.api.put(commandsPath(), dataToSend));
			final var commandsSet = new DiscordResourceMap<Command>();

			for (final var commandData : responseData) {
				final var command = new Command(client, commandData);
				commandsSet.put(command);
			}

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
			for (final var obj : JSON.parseObjectArray(client.api.get(commandsPath()))) {
				cache(obj);
			}
		});
	}

}
