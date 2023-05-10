package discord.client;

import java.util.concurrent.CompletableFuture;

import discord.enums.GatewayIntent;
import discord.managers.ApplicationCommandManager;
import discord.structures.Application;
import discord.structures.interactions.Interaction;
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
			.thenAcceptAsync((final var r) -> {
				application = new Application(this, r.toJsonObject());
				commands = new ApplicationCommandManager(this, null);
			});
	}

	@Override
	public void login(final String token, final GatewayIntent[] intents) {
		super.login(token, intents);
		fetchApplication();
	}
}
