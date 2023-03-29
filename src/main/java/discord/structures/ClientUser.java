package discord.structures;

import discord.client.DiscordClient;
import simple_json.JSONObject;

public class ClientUser extends User {

	public ClientUser(DiscordClient client, JSONObject data) {
		super(client, data);
	}	

}
