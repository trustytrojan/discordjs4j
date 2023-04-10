package command_manager;

import discord.client.BotDiscordClient;

class Discord {
	static final BotDiscordClient CLIENT = new BotDiscordClient();
	
	static {
		CLIENT.api.setToken("Bot " + discord.util.Util.readFile("./token"));
		CLIENT.fetchApplication().join();
	}
}
