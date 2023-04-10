package command_manager;

import discord.client.BotDiscordClient;

class Discord {
	static final BotDiscordClient client = new BotDiscordClient();
	static {
		client.api.setToken("Bot " + discord.util.Util.readFile("./token"));
		client.fetchApplication().join();
	}
}
