package test_bot;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.util.Util;

public class Main {
	public static void main(String[] __) {
		final var client = new BotDiscordClient();

		final GatewayIntent[] intents = {};

		client.ready.connect(() -> System.out.println("Logged in as " + client.user.tag() + '!'));

		client.messageCreate.connect((message) -> {
			switch (message.content()) {
				case "!ping" -> message.channel.send("pong nigga");
			}
		});

		client.chatInputInteractionCreate.connect((interaction) -> {
			switch (interaction.commandName) {
				case "ping" -> interaction.reply("`%sms`\nfrom member %s".formatted(client.latency(), interaction.member.nickname()));
			}
		});

		client.login(Util.readFile("tokens/java-bot"), intents);
	}
}
