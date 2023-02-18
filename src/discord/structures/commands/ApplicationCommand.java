package discord.structures.commands;

import discord.util.BetterJSONObject;
import discord.client.BotDiscordClient;
import discord.client.DiscordClient;
import discord.enums.CommandType;
import discord.structures.DiscordObject;

public class ApplicationCommand implements DiscordObject {
	
	private BetterJSONObject data;
	private final BotDiscordClient client;

	public ApplicationCommand(BotDiscordClient client, BetterJSONObject data) {
		this.client = client;
		this.data = data;
	}

	public CommandType type() {
		return CommandType.get(data.getLong("type"));
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}
	
	@Override
	public BetterJSONObject getData() {
		return data;
	}

	@Override
	public void setData(BetterJSONObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public String api_path() {
		return String.format("/applications/%s/commands/%s", client.application.id(), id());
	}

}
