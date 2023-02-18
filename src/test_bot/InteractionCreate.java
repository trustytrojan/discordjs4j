package test_bot;

import discord.client.BotDiscordClient;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;

final class InteractionCreate {

	private static final BotDiscordClient client = Main.client;

	static void listener(Interaction __) {
		switch(__) {
			case ChatInputInteraction ___ -> commandInteractionListener(___);
			default -> {}
		}
	}

	private static void commandInteractionListener(ChatInputInteraction interaction) {
		switch(interaction.commandName()) {
			case "ping" -> interaction.reply('`' + client.gateway.ping() + "ms`");
		}
	}

}
