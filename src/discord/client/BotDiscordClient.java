package discord.client;

import java.net.URISyntaxException;

import discord.enums.GatewayIntent;
import discord.managers.CommandManager;
import discord.structures.Application;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;

public class BotDiscordClient extends DiscordClient {

	public final Application application = new Application(this);

	public final CommandManager commands = new CommandManager(this);

	public final ClientEvent<Interaction> interactionCreate = new ClientEvent<>();
	public final ClientEvent<ChatInputInteraction> chatInputInteractionCreate = new ClientEvent<>();

	public BotDiscordClient() throws URISyntaxException {
		api.setBot(true);
	}

	public void login(String token, GatewayIntent[] intents) throws Exception {
		super.login(token, intents);
		application.fetch();
	}
	
}
