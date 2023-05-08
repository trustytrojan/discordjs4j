package java_bot;

import discord.structures.Embed;
import discord.structures.interactions.ChatInputInteraction;

public class ChatInputInteractionListener {
	public static void listener(final ChatInputInteraction interaction) {
		switch (interaction.commandName) {
			case "ping" -> interaction.respond("`%sms`");

			case "view_roles" -> {
				final var sb = new StringBuilder();
				for (final var role : interaction.member.roles)
					sb.append('\n' + role.mention());
				interaction.respond(sb.toString());
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
				interaction.respond(embed);
			}

			case "create_button_roles" -> {
				
			}
		}
	}
}
