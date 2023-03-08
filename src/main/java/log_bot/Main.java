package log_bot;

import discord.enums.GatewayIntent;
import discord.util.Util;

public class Main {

	static final LogBot client = new LogBot();
	private static final GatewayIntent[] intents = {
		GatewayIntent.Guilds,
		GatewayIntent.GuildModeration
	};

	static {
		client.login(Util.readFile("tokens/java-bot"), intents);

		client.ready.connect(() -> {
			System.out.printf("Logged in as %s!\n", client.user.tag());
			client._application.thenRun(() ->
				client.application.fetch().thenRun(() -> {
					System.out.println(client.application.owner());
				})
			);
		});

		client.guildCreate.connect((guild) -> System.out.printf("Received guild %s \"%s\"\n", guild.id(), guild.name()));

		client.auditLogEntryCreate.connect(AuditLogEntryCreate::listener);
		client.interactionCreate.connect(InteractionCreate::listener);
	}

	public static void main(String[] __) {}

}
