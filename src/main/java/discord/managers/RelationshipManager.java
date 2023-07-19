package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.client.UserDiscordClient;
import discord.resources.Relationship;
import discord.util.Util;
import sj.SjObject;

public class RelationshipManager extends ResourceManager<Relationship> {
	public RelationshipManager(DiscordClient client) {
		super(client, "/users/@me/relationships");
	}

	@Override
	public Relationship construct(SjObject data) {
		return new Relationship((UserDiscordClient) client, data);
	}

	public CompletableFuture<Void> block(String id) {
		return client.api.put(pathWithId(id), "{\"type\":2}").thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> addFriendWithId(String id) {
		return client.api.put(pathWithId(id), "{}").thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> addFriendWithUsername(String username) {
		return client.api.put(basePath, "{}").thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(pathWithId(id)).thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> fetch() {
		return client.api.get(basePath).thenAccept(r -> r.toJsonObjectArray().stream().forEach(this::cache));
	}
}
