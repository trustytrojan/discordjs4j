package discord.client;

import java.util.concurrent.CompletableFuture;

import discord.enums.GatewayIntent;
import discord.managers.ApplicationCommandManager;
import discord.resources.Application;
import discord.resources.interactions.Interaction;
import signals.Signal1;

public class BotDiscordClient extends DiscordClient {
	public Application application;
	public ApplicationCommandManager commands;

	public final Signal1<Interaction> interactionCreate = new Signal1<>();

	public BotDiscordClient() {
		api.setBot(true);
	}

	public CompletableFuture<Void> fetchApplication() {
		return api.get("/oauth2/applications/@me")
			.thenAccept(r -> {
				application = new Application(this, r.toJsonObject());
				commands = new ApplicationCommandManager(this, null);
			});
	}

	@Override
	public void login(String token, GatewayIntent[] intents) {
		super.login(token, intents);
		fetchApplication().join();
	}
}
