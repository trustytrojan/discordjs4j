package discord.structures.interactions;

import java.util.List;

import discord.util.BetterJSONObject;
import discord.util.BetterMap;

public class ChatInputInteractionOptionResolver {

	private final BetterMap<String, ChatInputInteractionOption> options = new BetterMap<>();
	
	public ChatInputInteractionOptionResolver(List<BetterJSONObject> raw_options) {
		for (final var option : raw_options)
			options.put(option.getString("name"), new ChatInputInteractionOption(option));
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
		return (String)get(name).value;
	}

	public Long getInteger(String name) {
		return (Long)get(name).value;
	}

	public Double getDouble(String name) {
		return (Double)get(name).value;
	}

	public Boolean getBoolean(String name) {
		return (Boolean)get(name).value;
	}

}
