package java_bot;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.util.Util;

public class JavaBot extends BotDiscordClient {
	public JavaBot() {
		ready.connect(() -> System.out.println("Logged in as " + user.tag() + '!'));

		chatInputInteractionCreate.connect((final var interaction) -> {
			switch (interaction.commandName) {
				case "ping" -> interaction.reply("`%sms`\nfrom member %s".formatted(gateway.ping(), interaction.member.nickname()));
				case "view_roles" -> {
					final var sb = new StringBuilder();
					for (final var role : interaction.member.roles.cache.values()) {
						sb.append('\n' + role.mention());
					}
					interaction.reply(sb.toString());
				}
				case "test_guild_command" -> {
					
				}
			}
		});
		
		messageComponentInteractionCreate.connect((final var interaction) -> {
			if (!interaction.isButton()) return;
			if (!interaction.inGuild()) return;

			final var guild = interaction.guild;
			final var member = interaction.member;
			//final var message = interaction.message;
			final var customId = interaction.customId;

			if (guild.roles.cache.containsKey(customId)) {
				if (member.roles.cache.containsKey(customId)) {
					member.roles.remove(customId)
						.thenRunAsync(() -> interaction.replyEphemeral("removed <@&"+customId+">!"));
				} else {
					member.roles.add(customId)
						.thenRunAsync(() -> interaction.replyEphemeral("added <@&"+customId+">!"));
				}
				return;
			}
			
			switch (customId) {
				case "test" -> interaction.reply("nigger nigger nigger nigger nigger nigger nigger");
			}
		});

		login(Util.readFile("tokens/java-bot"), new GatewayIntent[] {});
	}

	public static void main(String[] __) {
		new JavaBot();
	}
}
