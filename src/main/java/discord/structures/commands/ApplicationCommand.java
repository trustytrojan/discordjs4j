package discord.structures.commands;

import discord.client.BotDiscordClient;
import discord.client.DiscordClient;
import discord.enums.ApplicationCommandType;
import discord.structures.DiscordResource;
import simple_json.JSONObject;

public class ApplicationCommand implements DiscordResource {

	private final JSONObject data;
	private final BotDiscordClient client;

	public ApplicationCommand(BotDiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
	}

	public ApplicationCommandType type() {
		return ApplicationCommandType.get(data.getLong("type"));
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}

	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

}
