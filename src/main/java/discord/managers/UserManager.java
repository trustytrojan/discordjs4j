package discord.managers;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import discord.client.DiscordClient;
import discord.structures.ClientUser;
import discord.structures.User;

public class UserManager extends DataManager<User> {

	public UserManager(DiscordClient client) {
		super(client);
	}

	@Override
	public User cacheNewObject(JSONObject data) {
		return cacheObject(new User(client, data));
	}

	@Override
	public User fetch(String id, boolean force) {
		return super.fetch(id, "/users/" + id, force);
	}

	public ClientUser fetchMe() {
		final var data = JSON.parseObject(client.api.get("/users/@me"));
		final var me = new ClientUser(client, data);
		cache.put(me.id(), me);
		return me;
	}
	
}
