package discord.client;

import discord.enums.GatewayIntent;

public class UserDiscordClient extends DiscordClient {

	//public final RelationshipManager relationships = new RelationshipManager(this);

	public void login(String token, GatewayIntent[] intents) {
		super.login(token, intents);
		guilds.fetch().thenAccept((guilds) -> System.out.printf("[UserDiscordClient] Fetched %d guilds\n", guilds.size()));
	}
	
}
