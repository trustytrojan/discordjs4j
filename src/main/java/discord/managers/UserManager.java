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
		return construct(client.api.get("/users/" + id).toJSONObject());
	}

	public ClientUser fetchMe() {
		final var data = client.api.get("/users/@me").toJSONObject();
		final var me = new ClientUser(client, data);
		cache.put(me);
		return me;
	}
	
}
