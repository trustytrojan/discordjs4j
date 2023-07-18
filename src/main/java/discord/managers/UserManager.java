package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.CurrentUser;
import discord.resources.User;
import sj.SjObject;

public class UserManager extends ResourceManager<User> {
	public UserManager(DiscordClient client) {
		super(client);
	}

	@Override
	public User construct(SjObject data) {
		return new User(client, data);
	}

	@Override
	public CompletableFuture<User> get(String id, boolean force) {
		return super.get(id, "/users/" + id, force);
	}

	public CompletableFuture<CurrentUser> fetchMe() {
		return client.api.get("/users/@me")
			.thenApply(r -> {
				final var me = new CurrentUser(client, r.toJsonObject());
				return (CurrentUser) cache(me);
			});
	}
}
