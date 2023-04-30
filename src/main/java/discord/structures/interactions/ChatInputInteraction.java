package discord.structures.interactions;

import discord.client.BotDiscordClient;
import discord.structures.ApplicationCommand;
import simple_json.JSONObject;

public class ChatInputInteraction extends Interaction {
	public final ChatInputInteractionOptionResolver options;
	public final String commandId;
	public final String commandName;
	public final ApplicationCommand.Type commandType;

	public ChatInputInteraction(final BotDiscordClient client, final JSONObject data) {
		super(client, data);

		final var rawOptions = innerData.getObjectArray("options");
		options = (rawOptions == null) ? null : new ChatInputInteractionOptionResolver(this, rawOptions);

		commandId = innerData.getString("id");
		commandName = innerData.getString("name");
		commandType = ApplicationCommand.Type.resolve(innerData.getLong("type"));
	}
}
