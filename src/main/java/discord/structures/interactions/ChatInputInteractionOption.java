package discord.structures.interactions;

import discord.enums.CommandOptionType;
import simple_json.JSONObject;

public class ChatInputInteractionOption {
	
	public final CommandOptionType type;
	public final String name;
	public final Object value;
	public final ChatInputInteractionOptionResolver options;

	public ChatInputInteractionOption(JSONObject data) {
		type = CommandOptionType.resolve(data.getLong("type"));
		name = data.getString("name");
		value = data.get("value");
		final var raw_options = data.getObjectArray("options");
		ChatInputInteractionOptionResolver options = null;
		if (raw_options != null)
			options = new ChatInputInteractionOptionResolver(raw_options);
		this.options = options;
	}

}
