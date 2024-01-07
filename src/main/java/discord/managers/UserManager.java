package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.ClientUser;
import discord.resources.User;
import sj.SjObject;

public class UserManager extends ResourceManager<User> {
	public UserManager(DiscordClient client) {
		super(client, "/users");
	}

	@Override
	public User construct(SjObject data) {
		return new User(client, data);
	}

	public CompletableFuture<ClientUser> getCurrentUser() {
		return client.api.get("/users/@me").thenApply(r -> (ClientUser) cache(new ClientUser(client, r.asObject())));
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		throw new UnsupportedOperationException();
	}
}
