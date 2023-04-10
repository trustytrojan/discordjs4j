package discord.client;

import java.util.concurrent.CompletableFuture;

import discord.enums.GatewayIntent;
import discord.managers.ApplicationCommandManager;
import discord.structures.Application;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;
import java_signals.Signal1;

public final class BotDiscordClient extends DiscordClient {

	/**
	 * Will be null until logged in.
	 */
	public Application application;

	public final ApplicationCommandManager commands = new ApplicationCommandManager(this);

	public final Signal1<Interaction> interactionCreate = new Signal1<>();
	public final Signal1<ChatInputInteraction> chatInputInteractionCreate = new Signal1<>();

	public CompletableFuture<Void> fetchApplication() {
		return CompletableFuture.runAsync(
				() -> application = new Application(this, api.get("/oauth2/applications/@me").toJSONObject()));
	}

	public void login(String token, GatewayIntent[] intents) {
		if (!token.startsWith("Bot "))
			token = "Bot " + token;
		super.login(token, intents);
	}

}
