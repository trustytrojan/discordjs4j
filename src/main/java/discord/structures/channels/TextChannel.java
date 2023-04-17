package discord.structures.channels;

import discord.client.DiscordClient;
import simple_json.JSONObject;

public class TextChannel extends GuildTextBasedChannel {

	public TextChannel(DiscordClient client, JSONObject data) {
		super(client, data);
	}

}
