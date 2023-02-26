package log_bot;

import discord.structures.embed.Embed;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;

final class InteractionCreate {

	private static final LogBot client = Main.client;

	static void listener(Interaction interaction) {
		switch (interaction) {
			case ChatInputInteraction x -> chatInputInteractionListener(x);
			default -> {}
		}
	}

	private static String formatChannel(String channel) {
		return (channel != null) ? "<#"+channel+'>' : "(none)";
	}

	private static TGuild getTGuild(Interaction interaction) {
		final var guildId = interaction.guildId();
		return client.tguilds.ensure(guildId, () -> new TGuild(guildId));
	}

	private static void chatInputInteractionListener(ChatInputInteraction interaction) {
		switch (interaction.commandName()) {
			case "config" -> {
				final var subcommand = interaction.options.getSubcommand();
				final var options = subcommand.options;
				final var embed = new Embed();
				final var tguild = getTGuild(interaction);
				switch (subcommand.name) {
					case "audit_logger" -> {
						final var logging = tguild.logging;
						if (options.none()) {
							embed.setTitle("Audit logger settings");
							embed.addField("Enabled", logging.enabled ? "Yes" : "No");
							embed.addField("Channel", formatChannel(logging.channel));
						} else {
							final var enabled = options.getBoolean("enabled");
							final var channel = options.getString("channel");
							embed.setTitle("Changing audit logger settings");
							if (enabled != null) {
								final var oldValue = logging.enabled ? "Yes" : "No";
								final var newValue = enabled ? "Yes" : "No";
								embed.addField("Enabled", String.format("%s ➡️ %s", oldValue, newValue));
								logging.enabled = enabled;
							}
							if (channel != null) {
								final var oldValue = formatChannel(logging.channel);
								final var newValue = formatChannel(channel);
								embed.addField("Channel", String.format("%s ➡️ %s", oldValue, newValue));
								logging.channel = channel;
							}
						}
					}
				}
				interaction.reply(embed);
			}
		}
	}

}
