import java.util.Map;

import discord.client.BotDiscordClient;
import discord.resources.interactions.ChatInputInteraction;
import sj.SjSerializable;

public class ActivityTracker extends BotDiscordClient {
	private static class ATGuild implements SjSerializable {
		public final String guildId;
		public final Map<String, Integer> activityPerMember;

		@Override
		public String toJsonString() {
			
		}
	}

	private ActivityTracker() {
		ready.connect(() -> {
			System.out.println("Logged in as " + user.tag() + '!');
		});

		interactionCreate.connect(i -> {
			if (!(i instanceof final ChatInputInteraction interaction)) return;
			switch (interaction.commandName) {

			}
		});
	}
}
