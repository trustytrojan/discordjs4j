package discord.structures.interactions;

import discord.structures.ApplicationCommandOption;
import simple_json.JSONObject;

public class ChatInputInteractionOption {
	public final ApplicationCommandOption.Type type;
	public final String name;
	public final Object value;
	public final ChatInputInteractionOptionResolver options;

	public ChatInputInteractionOption(JSONObject data) {
		type = ApplicationCommandOption.Type.resolve(data.getLong("type"));
		name = data.getString("name");
		value = data.get("value");

		final var optionsData = data.getObjectArray("options");
		this.options = (optionsData != null) ? new ChatInputInteractionOptionResolver(optionsData) : null;
	}
}
