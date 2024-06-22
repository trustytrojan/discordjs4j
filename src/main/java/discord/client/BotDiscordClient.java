package discord.client;

import java.util.concurrent.CompletableFuture;

import discord.resources.Application;
import discord.structures.interactions.Interaction;

public non-sealed class BotDiscordClient extends DiscordClient {
	public final Application application;

	public BotDiscordClient(String token) {
		this(token, false);
	}

	public BotDiscordClient(String token, boolean debug) {
		super(token, true, debug);
		application = getApplication().join();
	}

	public CompletableFuture<Application> getApplication() {
		return api.get("/oauth2/applications/@me").thenApply(r -> new Application(this, r.asObject()));
	}

	protected void onInteractionCreate(Interaction interaction) {}
}
