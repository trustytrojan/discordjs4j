package java_bot;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.MessageComponentInteraction;
import discord.util.Util;

public class JavaBot extends BotDiscordClient {
	public JavaBot() {
		ready.connect(() -> System.out.println("Logged in as " + user.tag() + '!'));

		interactionCreate.connect((final var i) -> {
			if (i instanceof ChatInputInteraction cii)
				ChatInputInteractionListener.listener(cii);
			else if (i instanceof MessageComponentInteraction mci)
				MessageComponentInteractionListener.listener(mci);
		});

		login(Util.readFile("tokens/java-bot"), new GatewayIntent[] {});
	}

	public static void main(String[] __) {
		new JavaBot();
	}
}
