package discord.client;

import discord.enums.GatewayIntent;
import discord.managers.ApplicationCommandManager;
import discord.resources.Application;
import discord.resources.interactions.Interaction;
import signals.Signal1;

public class BotDiscordClient extends DiscordClient {
	public Application application;
	public ApplicationCommandManager commands;

	public final Signal1<Interaction> interactionCreate = new Signal1<>();

	public void login(String token, GatewayIntent[] intents) {
		api.setToken(token, true);
		final var r = api.get("/oauth2/applications/@me").join();
		application = new Application(this, r.toJsonObject());
		commands = new ApplicationCommandManager(this, null);
	}
}
