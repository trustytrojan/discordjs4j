package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.structures.ClientUser;
import discord.structures.User;
import discord.util.JSON;

public class UserManager extends DataManager<User> {

	public UserManager(DiscordClient client) {
		super(client);
	}

	@Override
	public User forceCache(BetterJSONObject data) {
		return cache(new User(client, data));
	}

	@Override
	public CompletableFuture<User> fetch(String id, boolean force) {
		final var path = String.format("/users/%s", id);
		return super.fetch(id, path, force);
	}

	public CompletableFuture<ClientUser> fetchMe() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				final var path = "/users/@me";
				final var data = JSON.parseObject(client.api.get(path));
				final var me = new ClientUser(client, data);
				cache.put(me.id(), me);
				return me;
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}
	
}
