package discord.structures.interactions;

import java.util.List;

import discord.util.BetterJSONObject;
import discord.util.BetterMap;

public class ChatInputInteractionOptions {

	private final BetterMap<String, Object> options = new BetterMap<>();
	
	public ChatInputInteractionOptions(List<BetterJSONObject> options_data) {
		for(final var option : options_data) {
			options.put(option.getString("name"), option.get("value"));
		}
	}

	public Object get(String name) {
		return options.get(name);
	}

	public String getString(String name) {
		return (String)get(name);
	}

	public Long getInteger(String name) {
		return (Long)get(name);
	}

	public Double getDouble(String name) {
		return (Double)get(name);
	}

	public Boolean getBoolean(String name) {
		return (Boolean)get(name);
	}

}
