package discord.managers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.resources.ApplicationCommand;
import discord.util.Util;
import sj.Sj;
import sj.SjObject;

public class ApplicationCommandManager extends ResourceManager<ApplicationCommand> {
	public ApplicationCommandManager(BotDiscordClient client, String guildId) {
		super(client, "/applications/" + client.user.id() + ((guildId != null) ? ("/guilds/" + guildId) : "") + "/commands");
	}

	@Override
	public ApplicationCommand construct(SjObject data) {
		return new ApplicationCommand((BotDiscordClient) client, data);
	}

	public CompletableFuture<ApplicationCommand> create(ApplicationCommand.Payload payload) {
		return client.api.post(basePath, payload.toJsonString())
			.thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<ApplicationCommand> edit(String id, ApplicationCommand.Payload payload) {
		return client.api.patch(pathWithId(id), payload.toJsonString())
			.thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(pathWithId(id)).thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> set(List<ApplicationCommand.Payload> commandPayloads) {
		cache.clear();
		return client.api.put(basePath, Sj.write(commandPayloads))
			.thenAccept(r -> r.toJsonObjectArray().forEach(this::cache));
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath).thenAccept(this::cacheNewDeleteOld);
	}
}
