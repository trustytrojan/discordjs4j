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
				case "test_2_integers" -> {
					final var int1 = interaction.options.getInteger("int1");
					final var int2 = interaction.options.getInteger("int2");
					interaction.reply("""
							%s
							%s
							(%d, %d)""".formatted(
								interaction.user.tag(),
								interaction.member.nickname(),
								int1,
								int2
							));
				}
			}
		});

		client.login(Util.readFile("tokens/java-bot"), intents);
	}
}
