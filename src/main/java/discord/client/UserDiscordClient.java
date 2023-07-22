package discord.client;

import discord.managers.RelationshipManager;

public class UserDiscordClient extends DiscordClient {
	public final RelationshipManager relationships = new RelationshipManager(this);

	public UserDiscordClient(String token) {
		super(token, false);
	}
}
