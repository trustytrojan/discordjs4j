package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.client.BotDiscordClient;
import discord.structures.commands.ApplicationCommand;
import discord.structures.commands.ApplicationCommandPayload;
import discord.util.JSON;

public class CommandManager extends DataManager<ApplicationCommand> {

	private final BotDiscordClient client;

	public CommandManager(BotDiscordClient client) {
		super(client);
		this.client = client;
	}

	@Override
	public ApplicationCommand forceCache(BetterJSONObject data) {
		return cache(new ApplicationCommand(client, data));
	}

	@Override
	public CompletableFuture<ApplicationCommand> fetch(String id, boolean force) {
		final var path = commandPath(id);
		return super.fetch(id, path, force);
	}

	public CompletableFuture<ApplicationCommand> create(ApplicationCommandPayload payload) {
		final var path = String.format("/applications/%s/commands", client.application.id());
		return CompletableFuture.supplyAsync(() -> {
			final var data = JSON.parseObject(client.api.post(path, payload.toJSONString()));
			return cache(data);
		});
	}

	public CompletableFuture<Void> delete(String id) {
		final var path = commandPath(id);
		return CompletableFuture.runAsync(() -> {
			client.api.delete(path);
			cache.remove(id);
		});
	}

	public CompletableFuture<Void> refresh() {
		cache.clear();
		final var path = String.format("/applications/%s/commands", client.application.id());
		return CompletableFuture.runAsync(() -> {
			final var data = JSON.parseObjectArray(client.api.get(path));
			for (final var obj : data)
				forceCache(obj);
		});
	}

	private String commandPath(String id) {
		return String.format("/applications/%s/commands/%s", client.application.id(), id);
	}

}
