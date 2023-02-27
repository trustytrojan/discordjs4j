package discord.structures.commands;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import discord.util.BetterJSONObject;
import discord.enums.CommandOptionType;
import discord.util.JSONable;

public class ApplicationCommandOptionPayload implements JSONable {

	public final CommandOptionType type;
	public final String name;
	public final String description;
	public final boolean required;
	public final List<ApplicationCommandOptionChoice> choices = new ArrayList<>();

	public ApplicationCommandOptionPayload(CommandOptionType type, String name, String description, boolean required) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.required = required;
	}

	public ApplicationCommandOptionPayload(CommandOptionType type, String name, String description) {
		this(type, name, description, false);
	}

	@Override
	public String toJSONString() {
		return toJSONObject().toJSONString();
	}

	@Override
	public JSONObject toJSONObject() {
		final var obj = new BetterJSONObject();
		obj.put("type", type.value);
		obj.put("name", name);
		obj.put("description", description);
		if (required) obj.put("required", required);
		return obj.innerObject;
	}

}
