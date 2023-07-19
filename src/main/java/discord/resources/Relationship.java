package discord.resources;

import java.util.concurrent.CompletableFuture;

import discord.client.UserDiscordClient;
import sj.SjObject;

public class Relationship extends AbstractDiscordResource {
	public static enum Type { NONE, FRIEND, BLOCKED, PENDING_INCOMING, PENDING_OUTGOING, IMPLICIT }

	private final UserDiscordClient client;

	public Relationship(UserDiscordClient client, SjObject data) {
		super(client, data, "/users/@me/relationships");
		this.client = client;
	}

	public CompletableFuture<Void> setType(Type type) {
		return client.relationships.setRelationshipType(id, type);
	}

	public CompletableFuture<Void> delete() {
		return client.relationships.delete(id);
	}

	public Type type() {
		return Type.values()[data.getInteger("type")];
	}

	public User user() {
		return new User(client, data.getObject("user"));
	}
}
