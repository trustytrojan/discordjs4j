package log_bot;

import discord.enums.GatewayIntent;
import discord.util.Util;

public class Main {

	static final LogBot client;
	private static final GatewayIntent[] intents = {
		GatewayIntent.Guilds,
		GatewayIntent.GuildModeration
	};

	static {
		try {
			client = new LogBot();
			client.login(Util.readFile("tokens/java-bot"), intents);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		client.ready.connect(() -> {
			System.out.printf("Logged in as %s!\n", client.user.tag());
			final var owner = client.application.owner();
			System.out.println(owner);
		});

		client.guildCreate.connect((guild) -> System.out.printf("Received guild %s \"%s\"\n", guild.id(), guild.name()));

		client.auditLogEntryCreate.connect(AuditLogEntryCreate::listener);
		client.interactionCreate.connect(InteractionCreate::listener);
	}

	public static void main(String[] __) {}

}
