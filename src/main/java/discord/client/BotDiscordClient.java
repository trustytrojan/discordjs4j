package discord.client;

import com.alibaba.fastjson2.JSON;

import discord.enums.GatewayIntent;
import discord.managers.CommandManager;
import discord.structures.ClientApplication;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;
import java_signals.Signal1;

public class BotDiscordClient extends DiscordClient {

	public final ClientApplication application = new ClientApplication(this);

	public final CommandManager commands = new CommandManager(this);

	public final Signal1<Interaction> interactionCreate = new Signal1<>();
	public final Signal1<ChatInputInteraction> chatInputInteractionCreate = new Signal1<>();

	public void login(String token, GatewayIntent[] intents) {
		if (!token.startsWith("Bot "))
			token = "Bot " + token;
		super.login(token, intents);
		application.setData(JSON.parseObject(api.get("/oauth2/applications/@me")));
	}
	
}
