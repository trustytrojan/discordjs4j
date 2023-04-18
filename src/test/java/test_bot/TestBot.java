package test_bot;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.util.Util;

public class TestBot {
	public static void main(String[] __) {
		final var client = new BotDiscordClient();

		final var token = Util.readFile("tokens/java-bot");
		final GatewayIntent[] intents = {};

		client.ready.connect(() -> System.out.println("Logged in as " + client.user.tag() + '!'));

		client.chatInputInteractionCreate.connect((interaction) -> {
			switch (interaction.commandName) {
				case "ping" -> interaction.reply("`%sms`\nfrom member %s".formatted(client.latency(), interaction.member.nickname()));
				case "view_roles" -> {
					final var sb = new StringBuilder();
					for (final var role : interaction.member.roles.cache.values()) {
						sb.append('\n' + role.mention());
					}
					interaction.reply(sb.toString());
				}
			}
		});

		client.login(token, intents);
	}
}
