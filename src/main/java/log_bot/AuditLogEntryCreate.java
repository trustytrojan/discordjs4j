package log_bot;

import discord.enums.ChannelType;
import discord.structures.AuditLogEntry;
import discord.structures.channels.TextBasedChannel;
import discord.structures.embed.Embed;
import discord.util.CDN;

import static log_bot.Main.client;

final class AuditLogEntryCreate {

	private static String multilineCodeblockOrNone(Object value) {
		return (value != null) ? ("```" + value + "```") : "(none)";
	}

	private static void channelChanges(Embed embed, AuditLogEntry log) {
		for (final var change : log.changes) {
			var key = change.key;
			var valueFormat = "`%s` ➡️ `%s`";
			String value = null;
			switch (key) {
				case "name" -> {
					key = "Name";
				}
				case "topic" -> {
					key = "Topic";
					final var oldTopic = multilineCodeblockOrNone(change.oldValue);
					final var newTopic = multilineCodeblockOrNone(change.newValue);
					value = oldTopic + "\n⬇️\n" + newTopic;
				}
			}
			if (value == null)
				embed.addField(key, String.format(valueFormat, change.oldValue, change.newValue));
			else
				embed.addField(key, value);
		}
	}

	private static void guildChanges(Embed embed, AuditLogEntry log) {
		for (final var change : log.changes) {
			var key = change.key;
			var valueFormat = "`%s` ➡️ `%s`";
			String value = null;
			switch (key) {
				case "name" -> {
					key = "Name";
				}
				case "system_channel_id" -> {
					key = "System Messages Channel";
					valueFormat = "<#%s> ➡️ <#%s>";
				}
				case "icon" -> {
					key = "Icon";
					final var guildId = log.targetId();
					final var oldIcon = CDN.guildIcon(guildId, (String)change.oldValue, 0, null);
					final var newIcon = CDN.guildIcon(guildId, (String)change.newValue, 0, null);
					value = String.format("[Old Icon](%s) ➡️ [New Icon](%s)", oldIcon, newIcon);
				}
				case "splash" -> {
					key = "Splash";
					final var guildId = log.targetId();
					final var oldSplash = CDN.guildIcon(guildId, (String)change.oldValue, 0, null);
					final var newSplash = CDN.guildIcon(guildId, (String)change.newValue, 0, null);
					value = String.format("[Old Splash](%s) ➡️ [New Splash](%s)", oldSplash, newSplash);
				}
			}
			if (value == null)
				embed.addField(key, String.format(valueFormat, change.oldValue, change.newValue));
			else
				embed.addField(key, value);
		}
	}

	static void listener(AuditLogEntry log) {
		final var tguild = client.tguilds.get(log.guildId());
		if (tguild == null) return;
		final var actionType = log.actionType();
		final var executor = log.executor();
		final var targetId = log.targetId();
		final var embed = new Embed();
		embed.setColor(LogEmbedColor.get(actionType));
		embed.setAuthor("By " + executor.tag(), executor.avatarURL(), null);
		switch (actionType) {
			case GuildUpdate -> {
				embed.setTitle("Server updated");
				guildChanges(embed, log);
			}

			case ChannelCreate -> {
				embed.setTitle("Channel created");
				final var type = ChannelType.resolve((Long)log.changes.get("type").newValue).toString();
				final var name = (String)log.changes.get("name").newValue;
				embed.addField("Type", type);
				embed.addField("Name", name);
			}

			case ChannelUpdate -> {
				embed.setTitle("Channel updated");
				embed.setDescription("<#" + targetId + '>');
				channelChanges(embed, log);
			}

			case ChannelDelete -> {
				embed.setTitle("Channel deleted");
				final var type = ChannelType.resolve((Long)log.changes.get("type").oldValue).toString();
				final var name = (String)log.changes.get("name").oldValue;
				embed.addField("Type", type);
				embed.addField("Name", name);
			}

			default -> {
				return;
			}
		}
		embed.setFooter("ID: " + targetId, null);
		client.channels.fetch(tguild.logging.channel).whenComplete((channel, ex) -> {
			if (channel == null || ex != null) {
				tguild.logging.channel = null;
				return;
			}
			((TextBasedChannel)channel).send(embed).whenComplete((msg, _ex) -> {
				if (_ex != null) {
					tguild.logging.channel = null;
				}
			});
		});
	}

}
