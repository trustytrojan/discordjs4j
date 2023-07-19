package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.CurrentUser;
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

	public CompletableFuture<CurrentUser> getCurrentUser() {
		return client.api.get("/users/@me").thenApply(r -> (CurrentUser) cache(new CurrentUser(client, r.toJsonObject())));
	}
}
