package discord.resources;

import java.util.concurrent.CompletableFuture;

import discord.client.UserDiscordClient;
import sj.SjObject;

public class Relationship extends AbstractDiscordResource {
	public static enum Type { NONE, FRIEND, BLOCKED, PENDING_INCOMING, PENDING_OUTGOING, IMPLICIT }

	private final UserDiscordClient client;

	public Relationship(UserDiscordClient client, SjObject data) {
		super(client, data);
		this.client = client;
	}

	public CompletableFuture<Void> delete() {
		return client.relationships.delete(getId());
	}

	@Override
	public String getApiPath() {
		throw new UnsupportedOperationException("Relationships cannot be fetched individually");
	}

	public Type type() {
		return Type.values()[data.getInteger("type")];
	}

	public CompletableFuture<User> getUserAsync() {
		return client.users.get(getId());
	}
}
