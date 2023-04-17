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
	public ApplicationCommandManager commands;

	public final Signal1<ChatInputInteraction> chatInputInteractionCreate = new Signal1<>();

	public CompletableFuture<Void> fetchApplication() {
		return api.get("/oauth2/applications/@me")
			.thenAcceptAsync((final var r) -> {
				application = new Application(this, r.toJSONObject());
				commands = new ApplicationCommandManager(this);
			});
	}
}
