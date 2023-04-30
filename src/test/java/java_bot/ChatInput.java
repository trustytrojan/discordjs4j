package java_bot;

import discord.structures.Embed;
import discord.structures.components.ActionRow;
import discord.structures.components.Button;
import discord.structures.components.CustomIdButton;
import discord.structures.interactions.ChatInputInteraction;

public final class ChatInput {
	public static void listener(final ChatInputInteraction interaction) {
		switch (interaction.commandName) {
			case "ping" -> interaction.reply("`%sms`");

			case "view_roles" -> {
				final var sb = new StringBuilder();
				for (final var role : interaction.member.roles)
					sb.append('\n' + role.mention());
				interaction.reply(sb.toString());
			}

			case "user_test" -> {
				final var user = interaction.options.getUser("user").join();
				final var embed = new Embed();
				embed.setAuthor("User info");
				embed.title = user.tag();
				embed.thumbnail = user.avatar.url();
				embed.addField("ID", '`' + user.id() + '`', true);
				embed.addField("Username", '`' + user.username() + '`', true);
				embed.addField("Discriminator", "`" + user.discriminator() + '`', true);
				if (user.banner.hash() != null) {
					embed.addField("Banner", "Shown below");
					embed.image = user.banner.url(4096);
				}
				interaction.reply(embed);
			}

			case "create_button_roles" -> {
				interaction.reply("test",
					new ActionRow(
						new CustomIdButton(Button.Style.PRIMARY, "test", "test"),
						new CustomIdButton(Button.Style.SECONDARY, "test2", "test"),
						new CustomIdButton(Button.Style.DANGER, "test3", "test"),
						new CustomIdButton(Button.Style.SUCCESS, "test4", "test")
					)
				);
			}
		}
	}
}
