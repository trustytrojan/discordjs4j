package discord.client;

import java.util.concurrent.CompletableFuture;

import discord.managers.ApplicationCommandManager;
import discord.structures.Application;
import discord.structures.interactions.ChatInputInteraction;
import java_signals.Signal1;

public final class BotDiscordClient extends DiscordClient {

	/**
	 * Will be null until logged in.
	 */
	public Application application;

	public final ApplicationCommandManager commands = new ApplicationCommandManager(this);

	public final Signal1<ChatInputInteraction> chatInputInteractionCreate = new Signal1<>();

	public CompletableFuture<Void> fetchApplication() {
		return CompletableFuture.runAsync(
				() -> application = new Application(this, api.get("/oauth2/applications/@me").join().toJSONObject()));
	}

}
