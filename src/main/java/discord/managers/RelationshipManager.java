package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.UserDiscordClient;
import discord.resources.Relationship;
import discord.util.Util;
import sj.SjObject;

public class RelationshipManager extends ResourceManager<Relationship> {
	public RelationshipManager(UserDiscordClient client) {
		super(client, "/users/@me/relationships");
	}

	@Override
	public Relationship construct(SjObject data) {
		return new Relationship((UserDiscordClient) client, data);
	}

	@Override
	public CompletableFuture<Relationship> get(String id, boolean force) {
		throw new UnsupportedOperationException("Discord does not allow GET requests to /users/@me/relationships/{id}");
	}

	public CompletableFuture<Void> blockUser(String id) {
		return client.api.put(pathWithId(id), "{\"type\":2}").thenRun(Util.NO_OP);
	}

	/**
	 * WARNING: This API method is heavily monitored by Discord. It is very likely
	 * that this will throw a {@code DiscordAPIException} with the response body
	 * containing captcha-related data. It is advised not to use this endpoint as a
	 * user.
	 * @param id ID of user to add as a friend
	 */
	public CompletableFuture<Void> addFriendWithId(String id) {
		return client.api.put(pathWithId(id), "{}").thenRun(Util.NO_OP);
	}

	/**
	 * WARNING: This API method is heavily monitored by Discord. It is very likely
	 * that this will throw a {@code DiscordAPIException} with the response body
	 * containing captcha-related data. It is advised not to use this endpoint as a
	 * user.
	 * @param username Username of user to add as a friend
	 */
	public CompletableFuture<Void> addFriendWithUsername(String username) {
		return client.api.put(basePath, "{}").thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(pathWithId(id)).thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath).thenAccept(r -> r.toJsonObjectArray().forEach(this::cache));
	}
}
