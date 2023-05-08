package java_bot;

import discord.structures.interactions.MessageComponentInteraction;

public class MessageComponentInteractionListener {
	public static void listener(final MessageComponentInteraction interaction) {
		if (!interaction.isButton()) return;
		if (!interaction.inGuild()) return;

		final var guild = interaction.guild;
		final var member = interaction.member;
		//final var message = interaction.message;
		final var customId = interaction.customId;

		if (guild.roles.cache.containsKey(customId)) {
			if (member.roles.cache.containsKey(customId)) {
				member.roles.remove(customId)
					.thenRunAsync(() -> interaction.respondEphemeral("removed <@&"+customId+">!"));
			} else {
				member.roles.add(customId)
					.thenRunAsync(() -> interaction.respondEphemeral("added <@&"+customId+">!"));
			}
			return;
		}
		
		switch (customId) {
			case "test" -> interaction.respond("test button pressed");
		}
	}
}
