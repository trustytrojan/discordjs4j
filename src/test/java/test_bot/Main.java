package test_bot;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.util.Util;

public class Main {
	public static void main(String[] __) {
		final var client = new BotDiscordClient();

		final GatewayIntent[] intents = {
			GatewayIntent.DIRECT_MESSAGES,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.MESSAGE_CONTENT
		};

		client.ready.connect(() -> System.out.println("Logged in as " + client.user.tag() + '!'));

		client.messageCreate.connect((message) -> {
			switch (message.content()) {
				case "!ping" -> message.channel.send("pong nigga");
			}
		});

		client.login(Util.readFile("token"), intents);
	}
}
