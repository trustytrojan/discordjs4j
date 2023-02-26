package log_bot;

import discord.client.BotDiscordClient;
import discord.structures.embed.Embed;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;

final class InteractionCreate {

	private static final BotDiscordClient client = Main.client;

	static void listener(Interaction __) {
		switch (__) {
			case ChatInputInteraction ___ -> commandInteractionListener(___);
			default -> {}
		}
	}

	private static void commandInteractionListener(ChatInputInteraction interaction) {
		final var options = interaction.options;
		final var user = interaction.user();
		switch (interaction.commandName()) {
			case "ping" -> interaction.reply('`' + client.gateway.ping() + "ms`");
			case "test_2" -> {
				final var int_option = options.getInteger("int_option");
				final var bool_option = options.getBoolean("bool_option");
				final var embed = new Embed();
				embed.setAuthor(user.tag(), user.avatarURL(), null);
				embed.setTitle("Test response");
				embed.addField("int_option", String.format("You sent `%s`", int_option));
				String bool_resp;
				if(bool_option != null)
					bool_resp = String.format("You sent `%s`", bool_option);
				else
					bool_resp = "You did not send this option";
				embed.addField("bool_option", bool_resp);
				interaction.reply(embed);
			}
		}
	}

}
