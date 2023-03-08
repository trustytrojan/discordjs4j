package discord.client;

import java.util.concurrent.CompletableFuture;

import discord.enums.GatewayIntent;
import discord.managers.CommandManager;
import discord.structures.ClientApplication;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;

public class BotDiscordClient extends DiscordClient {

	public final ClientApplication application = new ClientApplication(this);
	public CompletableFuture<Void> _application;

	public final CommandManager commands = new CommandManager(this);

	public final ClientEvent<Interaction> interactionCreate = new ClientEvent<>();
	public final ClientEvent<ChatInputInteraction> chatInputInteractionCreate = new ClientEvent<>();

	public BotDiscordClient() {
		api.setBot(true);
	}

	public void login(String token, GatewayIntent[] intents) {
		super.login(token, intents);
		_application = application.fetch();
	}
	
}
