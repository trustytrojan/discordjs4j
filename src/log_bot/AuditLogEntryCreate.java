package log_bot;

import discord.client.BotDiscordClient;
import discord.structures.AuditLogEntry;
import discord.structures.channels.TextBasedChannel;
import discord.structures.embed.Embed;

final class AuditLogEntryCreate {

	private static final BotDiscordClient client = Main.client;

	static void listener(AuditLogEntry log) {
		final var type = log.actionType();
		final var executor = log.executor();
		final var embed = new Embed();
		embed.setColor(LogEmbedColor.get(type));
		embed.setAuthor("By " + executor.tag(), executor.avatarURL(), null);
		switch (type) {
			case GuildUpdate -> {
				embed.setTitle("Server updated");
				for (final var change : log.changes) {
					var key = change.key();
					var valueFormat = "`%s` ➡️ `%s`";
					switch (key) {
						case "name" -> { key = "Name"; }
						case "system_channel_id" -> { key = "System Messages Channel"; valueFormat = "<#%s> ➡️ <#%s>"; }
						
					}
					embed.addField(key, String.format(valueFormat, change.oldValue(), change.newValue()));
				}
			}

			case ChannelCreate -> {
				embed.setTitle("Channel created");
				embed.addField("Name", '`' + (String)log.changes.get("name").newValue() + '`');
				embed.setFooter("ID: " + log.targetId(), null);
			}

			case ChannelDelete -> {
				embed.setTitle("Channel deleted");
				embed.addField("Name", '`' + (String)log.changes.get("name").oldValue() + '`');
				embed.setFooter("ID: " + log.targetId(), null);
			}

			default -> {}
		}
		client.channels.fetch("1071873742210342943").thenAccept((channel) -> ((TextBasedChannel)channel).send(embed));
	}

}
