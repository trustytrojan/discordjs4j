package discord.structures.interactions;

import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.structures.ApplicationCommand;
import discord.structures.InteractionReplyMessagePayload;
import discord.enums.InteractionCallbackType;
import simple_json.JSONObject;

public class ChatInputInteraction extends Interaction implements RepliableInteraction {
	public final ChatInputInteractionOptionResolver options;
	public final String commandId;
	public final String commandName;
	public final ApplicationCommand.Type commandType;

	public ChatInputInteraction(BotDiscordClient client, JSONObject data) {
		super(client, data);

		final var rawOptions = innerData.getObjectArray("options");
		options = (rawOptions != null) ? new ChatInputInteractionOptionResolver(rawOptions) : null;

		commandId = innerData.getString("id");
		commandName = innerData.getString("name");
		commandType = ApplicationCommand.Type.resolve(innerData.getLong("type"));
	}

	public CompletableFuture<Void> _reply(InteractionCallbackType type, InteractionReplyMessagePayload payload) {
		final var path = "/interactions/" + id + '/' + token + "/callback";
		final var data = new JSONObject();
		data.put("type", type.value);
		data.put("data", payload);
		return CompletableFuture.runAsync(() -> client.api.post(path, data.toJSONString()));
	}
}
