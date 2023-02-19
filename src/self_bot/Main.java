package self_bot;

import discord.client.UserDiscordClient;
import discord.enums.GatewayIntent;
import discord.util.Util;

public class Main {
	
	static final UserDiscordClient client;
	private static final GatewayIntent[] intents = {
		GatewayIntent.DirectMessages
	};

	static {
		try {
			client = new UserDiscordClient();
			client.login(Util.readFile("tokens/t_"), intents);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		client.ready.connect(() -> System.out.printf("Logged in as %s!\n", client.user.tag()));

		client.messageCreate.connect((message) -> {
			final var content = message.content();
			final var channel = message.channel();
			switch (content) {
				case "nigga" -> {
					String resp = "";
					for(int i = 0; i < 154; ++i) resp += "nigga";
					channel.send(resp);
				}
			}
		});
	}

	public static void main(String[] __) {}

}
