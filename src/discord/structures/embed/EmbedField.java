package discord.structures.embed;

import org.json.simple.JSONObject;

import discord.util.JSONable;

public record EmbedField(
	String name,
	String value,
	boolean inline
) implements JSONable {

	public EmbedField(String name, String value) {
		this(name, value, false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		final var obj = new JSONObject();
		obj.put("name", name);
		obj.put("value", value);
		obj.put("inline", inline);
		return obj;
	}

}
