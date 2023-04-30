package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.ClientUser;
import discord.structures.User;
import simple_json.JSONObject;

public class UserManager extends ResourceManager<User> {
	public UserManager(final DiscordClient client) {
		super(client);
	}

	@Override
	public User construct(final JSONObject data) {
		return new User(client, data);
	}

	@Override
	public CompletableFuture<User> fetch(final String id, final boolean force) {
		return super.fetch(id, "/users/" + id, force);
	}

	public CompletableFuture<ClientUser> fetchMe() {
		return client.api.get("/users/@me").thenApplyAsync((final var r) -> {
			final var me = new ClientUser(client, r.toJSONObject());
			return (ClientUser) cache(me);
		});
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		throw new UnsupportedOperationException("Global users cache cannot be refreshed");
	}
}
