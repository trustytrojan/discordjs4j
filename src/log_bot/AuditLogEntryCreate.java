package log_bot;

import discord.client.BotDiscordClient;
import discord.structures.AuditLogEntry;
import discord.structures.channels.TextBasedChannel;
import discord.structures.embed.Embed;

final class AuditLogEntryCreate {

	private static final BotDiscordClient client = Main.client;

	static void listener(AuditLogEntry log) {
		final var type = log.action_type();
		final var guild = log.guild();
		final var executor = log.executor();
		final var embed = new Embed();
		embed.setColor(LogEmbedColor.get(type));
		embed.setAuthor("By " + executor.tag(), executor.avatarURL(), null);
		switch (type) {
			case ChannelCreate -> {
				embed.setTitle("Channel created");
				try {
					final var channel = client.channels.fetch(log.target_id()).get();
					embed.addField("Name", '`' + channel.name() + '`');
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}

			case ChannelDelete -> {
				embed.setTitle("Channel deleted");
				embed.addField("Name", '`' + log.changes.get("name").old_value().toString() + '`');
				embed.setFooter("ID: " + log.target_id(), null);
			}

			default -> {}
		}
		client.channels.fetch("1071873742210342943").thenAccept((channel) -> ((TextBasedChannel)channel).send(embed));
	}

}
