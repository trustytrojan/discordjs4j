import discord.client.BotDiscordClient;
import discord.resources.interactions.ChatInputInteraction;

public class ActivityTracker extends BotDiscordClient {
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
