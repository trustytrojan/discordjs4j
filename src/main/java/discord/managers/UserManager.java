package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.ClientUser;
import discord.structures.User;
import simple_json.SjObject;

public class UserManager extends ResourceManager<User> {
	public UserManager(DiscordClient client) {
		super(client);
	}

	@Override
	public User construct(SjObject data) {
		return new User(client, data);
	}

	@Override
	public CompletableFuture<User> fetch(String id, boolean force) {
		return super.fetch(id, "/users/" + id, force);
	}

	public CompletableFuture<ClientUser> fetchMe() {
		return client.api.get("/users/@me")
			.thenApplyAsync(r -> {
				final var me = new ClientUser(client, r.toJsonObject());
				return (ClientUser) cache(me);
			});
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		throw new UnsupportedOperationException("Global users cache cannot be refreshed");
	}
}
