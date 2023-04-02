package test_bot;

import discord.client.DiscordClient;
import discord.client.Gateway;
import discord.util.Util;

public class Main {

	public static void main(String[] __) {
		final var client = new DiscordClient.Bot();

		final Gateway.Intent[] intents = {
			Gateway.Intent.DIRECT_MESSAGES,
			Gateway.Intent.GUILD_MESSAGES,
			Gateway.Intent.MESSAGE_CONTENT
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
