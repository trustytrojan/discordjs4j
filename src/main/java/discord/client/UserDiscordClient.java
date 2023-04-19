package discord.client;

public class UserDiscordClient extends DiscordClient {
	// public final RelationshipManager relationships = new RelationshipManager(this);

	public UserDiscordClient() {
		api.setBot(false);
	}
}
