package discord.structures.interactions;

import discord.structures.Application.Command;
import simple_json.JSONObject;

public class ChatInputInteractionOption {
	
	public final Command.Option.Type type;
	public final String name;
	public final Object value;
	public final ChatInputInteractionOptionResolver options;

	public ChatInputInteractionOption(JSONObject data) {
		type = Command.Option.Type.resolve(data.getLong("type"));
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
