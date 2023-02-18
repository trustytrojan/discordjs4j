package test_bot;

import discord.client.BotDiscordClient;
import discord.structures.AuditLogEntry;
import discord.structures.embed.Embed;

final class AuditLogEntryCreate {

	private static final BotDiscordClient client = Main.client;
	
	static void listener(AuditLogEntry log) {
		final var executor = log.executor();

		final var embed = new Embed();
		embed.setAuthor("By "+executor.tag(), executor.avatarURL(), null);

		switch(log.action_type()) {
			case ChannelCreate -> {
				embed.setTitle("Channel created");
				try {
					final var channel = client.channels.fetch(log.target_id()).get();
					embed.addField("Name", '`'+channel.name()+'`');
				} catch(Exception e) { e.printStackTrace(); return; }
			}

			case ChannelDelete -> {
				embed.setTitle("Channel deleted");
				embed.addField("Name", '`'+log.changes.get("name").old_value().toString()+'`');
				embed.setFooter("ID: "+log.target_id(), null);
			}

			default -> {}
		}

		//channel.send(embed);
	}

}
