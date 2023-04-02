package discord.managers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONArray;

import discord.client.BotDiscordClient;
import discord.structures.commands.ApplicationCommand;
import discord.structures.commands.ApplicationCommandPayload;
import discord.util.DiscordResourceMap;
import simple_json.JSON;
import simple_json.JSONObject;

public class ApplicationCommandManager extends DataManager<ApplicationCommand> {

	private final BotDiscordClient client;

	public ApplicationCommandManager(BotDiscordClient client) {
		super(client);
		this.client = client;
	}

	@Override
	public ApplicationCommand cache(JSONObject data) {
		return cache(new ApplicationCommand(client, data));
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

	public CompletableFuture<ApplicationCommand> create(ApplicationCommandPayload payload) {
		return CompletableFuture.supplyAsync(() -> {
			final var data = JSON.parseObject(client.api.post(commandsPath(), payload.toString()));
			return cache(data);
		});
	}

	public CompletableFuture<DiscordResourceMap<ApplicationCommand>> set(List<ApplicationCommandPayload> commands) {
		final var dataToSend = commands.toString();
		return CompletableFuture.supplyAsync(() -> {
			final var responseData = JSON.parseObjectArray(client.api.put(commandsPath(), dataToSend));
			final var commandsSet = new DiscordResourceMap<ApplicationCommand>();

			for (final var commandData : responseData) {
				final var command = new ApplicationCommand(client, commandData);
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
