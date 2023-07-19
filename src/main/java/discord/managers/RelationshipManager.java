package discord.managers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.client.UserDiscordClient;
import discord.resources.Relationship;
import discord.util.Util;
import sj.SjObject;

public class RelationshipManager extends ResourceManager<Relationship> {
	private static final String basePath = "/users/@me/relationships";

	private static String pathWithId(String id) {
		return basePath + '/' + id;
	}

	public RelationshipManager(DiscordClient client) {
		super(client);
	}

	@Override
	public Relationship construct(SjObject data) {
		return new Relationship((UserDiscordClient) client, data);
	}

	public CompletableFuture<Void> setRelationshipType(String id, Relationship.Type type) {
		return client.api.patch(pathWithId(id), """
				{
					"type": %s
				}
				""".formatted(type.ordinal())).thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(pathWithId(id)).thenRun(Util.NO_OP);
	}

	@Override
	public CompletableFuture<Relationship> get(String id, boolean force) {
		return super.get(id, pathWithId(id), force);
	}

	public CompletableFuture<List<Relationship>> fetch() {
		return client.api.get(basePath).thenApply(r -> r.toJsonObjectArray().stream().map(this::cache).toList());
	}
}
