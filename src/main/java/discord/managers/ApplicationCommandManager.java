package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.structures.commands.ApplicationCommand;
import discord.structures.commands.ApplicationCommandPayload;
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
