package discord.client;

import java.util.concurrent.CompletableFuture;

import discord.resources.Application;
import discord.resources.interactions.Interaction;

public class BotDiscordClient extends DiscordClient {
	public final Application application;

	public BotDiscordClient(String token) {
		super(token, true);
		application = getApplication().join();
	}

	public CompletableFuture<Application> getApplication() {
		return api.get("/oauth2/applications/@me").thenApply(r -> new Application(this, r.toJsonObject()));
	}

	/*
	 * Subclasses should override the below methods as necessary
	 * if they want to use old-style signal handling.
	 */

	protected void onInteractionCreate(Interaction interaction) {}
}
