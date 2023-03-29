package discord.structures;

import com.alibaba.fastjson2.JSONObject;

import discord.client.DiscordClient;

public class ClientUser extends User {

	public ClientUser(DiscordClient client, JSONObject data) {
		super(client, data);
	}	

}
