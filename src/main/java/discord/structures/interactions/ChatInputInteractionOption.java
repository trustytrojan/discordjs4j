package discord.structures.interactions;

import discord.enums.ApplicationCommandOptionType;
import simple_json.JSONObject;

public class ChatInputInteractionOption {
	
	public final ApplicationCommandOptionType type;
	public final String name;
	public final Object value;
	public final ChatInputInteractionOptionResolver options;

	public ChatInputInteractionOption(JSONObject data) {
		type = ApplicationCommandOptionType.resolve(data.getLong("type"));
		name = data.getString("name");
		value = data.get("value");

		final var optionsData = data.getObjectArray("options");
		ChatInputInteractionOptionResolver options = null;
		if (optionsData != null) {
			options = new ChatInputInteractionOptionResolver(optionsData);
		}
		this.options = options;
	}

}
