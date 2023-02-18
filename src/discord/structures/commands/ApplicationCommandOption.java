package discord.structures.commands;

import java.util.ArrayList;
import java.util.List;

import discord.util.BetterJSONObject;
import discord.enums.CommandOptionType;

public class ApplicationCommandOption {
	
	private BetterJSONObject data;
	public final List<ApplicationCommandOption> options = new ArrayList<>();

	public ApplicationCommandOption(BetterJSONObject data) {
		this.data = data;
	}

	public CommandOptionType type() {
		return CommandOptionType.get(data.getLong("type"));
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}

}
