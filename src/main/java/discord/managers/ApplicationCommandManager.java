package discord.managers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.resources.ApplicationCommand;
import sj.Sj;
import sj.SjObject;

public class ApplicationCommandManager extends ResourceManager<ApplicationCommand> {
	private final String basePath;

	public ApplicationCommandManager(BotDiscordClient client, String guildId) {
		super(client);
		final var start = "/applications/" + client.application.id();
		basePath = start + ((guildId != null)
							? "/guilds/" + guildId + "/commands"
							: "/commands");
	}

	@Override
	public ApplicationCommand construct(SjObject data) {
		return new ApplicationCommand((BotDiscordClient) client, data);
	}

	@Override
	public CompletableFuture<ApplicationCommand> get(String id, boolean force) {
		return super.get(id, basePath + '/' + id, force);
	}

	public CompletableFuture<ApplicationCommand> create(ApplicationCommand.Payload payload) {
		return client.api.post(basePath, payload.toJsonString())
			.thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<ApplicationCommand> edit(String id, ApplicationCommand.Payload payload) {
		return client.api.patch(basePath + '/' + id, payload.toJsonString())
			.thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(basePath + '/' + id).thenRun(() -> cache.remove(id));
	}

	public CompletableFuture<Void> set(List<ApplicationCommand.Payload> commandPayloads) {
		cache.clear();
		return client.api.put(basePath, Sj.write(commandPayloads))
			.thenAccept(r -> r.toJsonObjectArray().forEach(this::cache));
	}

	public CompletableFuture<Void> refreshCache() {
		cache.clear();
		return client.api.get(basePath)
			.thenAccept(r -> r.toJsonObjectArray().forEach(this::cache));
	}
}
