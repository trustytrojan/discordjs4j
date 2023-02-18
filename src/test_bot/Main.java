package test_bot;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.util.Util;

public class Main {

	static final BotDiscordClient client;
	private static final GatewayIntent[] intents = {
		GatewayIntent.Guilds,
		GatewayIntent.GuildModeration
	};

	static {
		try {
			client = new BotDiscordClient();
			client.login(Util.readFile("token"), intents);
		} catch(Exception e) { throw new RuntimeException(e); }

		client.ready.connect(() -> System.out.println("Logged in as "+client.user.tag()+"!"));

		client.guildCreate.connect((guild) -> {
			System.out.printf("Received guild %s %s\n", guild.id(), guild.name());
		});

		client.auditLogEntryCreate.connect(AuditLogEntryCreate::listener);
		client.interactionCreate.connect(InteractionCreate::listener);
	}

	public static void main(String[] __) {}
	
}
