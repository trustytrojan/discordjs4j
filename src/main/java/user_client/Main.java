package user_client;

import discord.client.UserDiscordClient;
import discord.enums.GatewayIntent;
import discord.util.Util;

public class Main {
	
	static final UserDiscordClient client = new UserDiscordClient();
	private static final GatewayIntent[] intents = {
		GatewayIntent.DirectMessages
	};

	static {
		client.login(Util.readFile("tokens/main"), intents);

		client.ready.connect(() -> {
			System.out.printf("Logged in as %s!\n", client.user.tag());
		});

		client.messageCreate.connect((message) -> {
			System.out.printf("%s: %s: %s\n", message.channel().name(), message.author().tag(), message.content());
		});
	}

	public static void main(String[] __) {}

}
