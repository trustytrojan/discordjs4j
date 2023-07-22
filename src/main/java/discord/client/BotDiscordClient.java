package discord.client;

import java.util.concurrent.CompletableFuture;

import discord.resources.Application;
import discord.resources.interactions.Interaction;
import signals.Signal1;

public class BotDiscordClient extends DiscordClient {
	public final Signal1<Interaction> interactionCreate = new Signal1<>();
	public final Application application;

	public BotDiscordClient(String token) {
		super(token, true);
		application = getApplication().join();
	}

	public CompletableFuture<Application> getApplication() {
		return api.get("/oauth2/applications/@me").thenApply(r -> new Application(this, r.toJsonObject()));
	}
}
