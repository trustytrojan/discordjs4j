package discord.structures.interactions;

import discord.util.BetterMap;
import simple_json.JSONObject;

public class ChatInputInteractionOptionResolver {

	private final BetterMap<String, ChatInputInteractionOption> options = new BetterMap<>();
	
	public ChatInputInteractionOptionResolver(JSONObject[] raw_options) {
		for (final var option : raw_options)
			options.put(option.getString("name"), new ChatInputInteractionOption(option));
	}

	public boolean none() {
		return (options.size() == 0);
	}

	public ChatInputInteractionOption get(String name) {
		return options.get(name);
	}

	/**
	 * If a subcommand was used, it will be the only option
	 * @return the name of the subcommand
	 */
	public ChatInputInteractionOption getSubcommand() {
		return options.first();
	}

	public String getString(String name) {
		final var option = get(name);
		return (option != null) ? (String)option.value : null;
	}

	public Long getInteger(String name) {
		final var option = get(name);
		return (option != null) ? (Long)option.value : null;
	}

	public Double getDouble(String name) {
		final var option = get(name);
		return (option != null) ? (Double)option.value : null;
	}

	public Boolean getBoolean(String name) {
		final var option = get(name);
		return (option != null) ? (Boolean)option.value : null;
	}

}
