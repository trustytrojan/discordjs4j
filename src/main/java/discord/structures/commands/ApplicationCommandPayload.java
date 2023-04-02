package discord.structures.commands;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

import discord.enums.ApplicationCommandType;
import simple_json.JSONObject;

public class ApplicationCommandPayload implements JSONAware {
	
	public ApplicationCommandType type;
	public String name;
	public String description;
	public final List<ApplicationCommandOptionPayload> options = new ArrayList<>();

	public ApplicationCommandPayload(ApplicationCommandType type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}

	public ApplicationCommandPayload(String name, String description) {
		this(null, name, description);
	}

	@Override
	public String toJSONString() {
		final var obj = new JSONObject();

		if (type != null) {
			obj.put("type", type.value);
		}

		obj.put("name", name);
		obj.put("description", description);

		if (options.size() > 0) {
			obj.put("options", (JSONArray) options);
		}

		return obj.toString();
	}

}
