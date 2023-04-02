package discord.structures.commands;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

import discord.enums.ApplicationCommandOptionType;
import simple_json.JSONObject;

public class ApplicationCommandOptionPayload implements JSONAware {

	public ApplicationCommandOptionType type;
	public String name;
	public String description;
	public boolean required;
	public final List<ApplicationCommandOptionChoice> choices = new ArrayList<>();

	public ApplicationCommandOptionPayload(ApplicationCommandOptionType type, String name, String description, boolean required) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.required = required;
	}

	public ApplicationCommandOptionPayload(ApplicationCommandOptionType type, String name, String description) {
		this(type, name, description, false);
	}

	@Override
	public String toJSONString() {
		final var obj = new JSONObject();

		obj.put("type", type.value);
		obj.put("name", name);
		obj.put("description", description);

		if (required) {
			obj.put("required", required);
		}

		if (choices.size() > 0) {
			obj.put("choices", (JSONArray) choices);
		}

		return obj.toString();
	}

}
