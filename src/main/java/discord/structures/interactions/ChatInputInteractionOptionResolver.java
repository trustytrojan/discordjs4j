package discord.structures.interactions;

import java.util.HashMap;
import java.util.List;

import simple_json.JSONObject;

public class ChatInputInteractionOptionResolver {

	private final HashMap<String, ChatInputInteractionOption> options = new HashMap<>();

	public ChatInputInteractionOptionResolver(List<JSONObject> data) {
		for (final var optionData : data) {
			final var option = new ChatInputInteractionOption(optionData);
			options.put(option.name, option);
		}
	}

	public boolean none() {
		return (options.size() == 0);
	}

	public ChatInputInteractionOption get(String name) {
		return options.get(name);
	}

	/**
	 * If a subcommand was used, it will be the only option
	 * 
	 * @return the name of the subcommand
	 */
	public ChatInputInteractionOption getSubcommand() {
		return options.entrySet().iterator().next().getValue();
	}

	public String getString(String name) {
		final var option = get(name);
		return (option != null) ? ((String) option.value) : null;
	}

	public Long getInteger(String name) {
		final var option = get(name);
		return (option != null) ? ((Long) option.value) : null;
	}

	public Double getDouble(String name) {
		final var option = get(name);
		return (option != null) ? ((Double) option.value) : null;
	}

	public Boolean getBoolean(String name) {
		final var option = get(name);
		return (option != null) ? ((Boolean) option.value) : null;
	}

}
