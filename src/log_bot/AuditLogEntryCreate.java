package log_bot;

import discord.structures.AuditLogEntry;
import discord.structures.channels.TextBasedChannel;
import discord.structures.embed.Embed;

import static log_bot.Main.client;

final class AuditLogEntryCreate {

	private static void channelChanges(Embed embed, AuditLogEntry log) {
		for (final var change : log.changes) {
			var key = change.key;
			var valueFormat = "`%s` ➡️ `%s`";
			switch (key) {
				case "name" -> { key = "Name"; }
				case "topic" -> { key = "Topic"; valueFormat = "```%s``` ⬇️ ```%s```"; }

			}
			embed.addField(key, String.format(valueFormat, change.oldValue, change.newValue));
		}
	}

	private static void guildChanges(Embed embed, AuditLogEntry log) {
		for (final var change : log.changes) {
			var key = change.key;
			var valueFormat = "`%s` ➡️ `%s`";
			switch (key) {
				case "name" -> { key = "Name"; }
				case "system_channel_id" -> { key = "System Messages Channel"; valueFormat = "<#%s> ➡️ <#%s>"; }
				
			}
			embed.addField(key, String.format(valueFormat, change.oldValue, change.newValue));
		}
	}

	static void listener(AuditLogEntry log) {
		final var tguild = client.tguilds.get(log.guildId());
		if (tguild == null) return;
		final var type = log.actionType();
		final var executor = log.executor();
		final var embed = new Embed();
		embed.setColor(LogEmbedColor.get(type));
		embed.setAuthor("By " + executor.tag(), executor.avatarURL(), null);
		switch (type) {
			case GuildUpdate -> {
				embed.setTitle("Server updated");
				guildChanges(embed, log);
			}

			case ChannelCreate -> {
				embed.setTitle("Channel created");
				embed.addField("Name", '`' + (String)log.changes.get("name").newValue + '`');
			}

			case ChannelUpdate -> {
				embed.setTitle("Channel updated");
				channelChanges(embed, log);
			}

			case ChannelDelete -> {
				embed.setTitle("Channel deleted");
				embed.addField("Name", '`' + (String)log.changes.get("name").oldValue + '`');
				embed.setFooter("ID: " + log.targetId(), null);
			}

			default -> {}
		}
		embed.setFooter("ID: " + log.targetId(), null);
		client.channels.fetch(tguild.logging.channel).thenAccept((channel) -> ((TextBasedChannel)channel).send(embed));
	}

}
