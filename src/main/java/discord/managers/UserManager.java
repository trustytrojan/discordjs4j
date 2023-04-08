package discord.managers;

import discord.client.DiscordClient;
import discord.structures.ClientUser;
import discord.structures.User;
import simple_json.JSONObject;

public class UserManager extends DataManager<User> {

	public UserManager(DiscordClient client) {
		super(client);
	}

	@Override
	public User construct(JSONObject data) {
		return new User(client, data);
	}

	@Override
	public User fetch(String id, boolean force) {
		return super.fetch(id, "/users/" + id, force);
	}

	public ClientUser fetchMe() {
		final var data = client.api.get("/users/@me").toJsonObject();
		final var me = new ClientUser(client, data);
		cache.put(me);
		return me;
	}
	
}
