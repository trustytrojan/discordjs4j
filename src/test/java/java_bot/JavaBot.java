package java_bot;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.util.Util;

public class JavaBot extends BotDiscordClient {
	public JavaBot() {
		ready.connect(() -> System.out.println("Logged in as " + user.tag() + '!'));

		chatInputInteractionCreate.connect(ChatInputInteractionListener::listener);
		messageComponentInteractionCreate.connect(MessageComponentInteractionListener::listener);

		login(Util.readFile("tokens/java-bot"), new GatewayIntent[] {});
	}

	public static void main(String[] __) {
		new JavaBot();
	}
}
