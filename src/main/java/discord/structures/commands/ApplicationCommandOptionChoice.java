package discord.structures.commands;

import org.json.simple.JSONAware;

import simple_json.JSONObject;

/**
 * value should be one of String, Long, or Double
 */
public record ApplicationCommandOptionChoice(String name, Object value) implements JSONAware {

	@Override
	public String toJSONString() {
		final var obj = new JSONObject();
		obj.put("name", name);
		obj.put("value", value);
		return obj.toString();
	}

}
