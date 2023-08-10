package discord.client;

import discord.managers.RelationshipManager;

public non-sealed class UserDiscordClient extends DiscordClient {
	public final RelationshipManager relationships = new RelationshipManager(this);

	public UserDiscordClient(String token) {
		this(token, false);
	}

	public UserDiscordClient(String token, boolean debug) {
		super(token, false, debug);
	}
}
