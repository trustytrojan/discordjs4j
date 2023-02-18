package discord.structures.commands;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import discord.util.BetterJSONObject;
import discord.enums.CommandType;
import discord.util.JSON;
import discord.util.JSONable;

public class ApplicationCommandPayload implements JSONable {
	
	private CommandType type;
	private String name;
	private String description;
	public final List<ApplicationCommandOptionPayload> options = new ArrayList<>();

	public ApplicationCommandPayload(CommandType type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}

	public ApplicationCommandPayload(String name, String description) {
		this(null, name, description);
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
		if(options.size() > 0) obj.put("options", JSON.buildArray(options));
		return obj.innerObject;
	}

}
