package discord.structures.interactions;

import discord.enums.CommandOptionType;
import discord.util.BetterJSONObject;

public class ChatInputInteractionOption {
	
	public final CommandOptionType type;
	public final String name;
	public final Object value;
	public final ChatInputInteractionOptionResolver options;

	public ChatInputInteractionOption(BetterJSONObject data) {
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
