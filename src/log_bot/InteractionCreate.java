package log_bot;

import discord.structures.embed.Embed;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;

final class InteractionCreate {

	private static final LogBot client = Main.client;

	static void listener(Interaction __) {
		switch (__) {
			case ChatInputInteraction ___ -> chatInputInteractionListener(___);
			default -> {}
		}
	}

	private static void chatInputInteractionListener(ChatInputInteraction interaction) {
		switch (interaction.commandName()) {
			case "config" -> {
				final var guild = interaction.guild();
				final var subcommand = interaction.options.getSubcommand();
				final var options = subcommand.options;
				final var embed = new Embed();
				final var tguild = client.tguilds.get(interaction.guildId());
				
				switch (subcommand.name) {
					case "audit_logger" -> {

						embed.setTitle("Changing audit logger settings");
						final var enabled = options.getBoolean("enabled");
						final var channel = (String)options.get("channel").value;
						if (enabled != null)
							embed.addField("Enabled", String.format("Set to `%b`", enabled));
						if (channel != null)
							embed.addField("Channel", String.format("Set to <#%s>", channel));
					}
				}
				interaction.reply(embed);
			}
		}
	}

}
