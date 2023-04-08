package discord.structures.interactions;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.ApplicationCommand;
import discord.structures.InteractionReplyMessagePayload;
import discord.enums.InteractionCallbackType;
import simple_json.JSONObject;

public class ChatInputInteraction extends Interaction implements RepliableInteraction {

	public final ChatInputInteractionOptionResolver options;
	public final String commandId;
	public final String commandName;
	public final ApplicationCommand.Type commandType;

	public ChatInputInteraction(DiscordClient client, JSONObject data) {
		super(client, data);

		final var raw_options = innerData.getObjectArray("options");
		options = (raw_options != null) ? new ChatInputInteractionOptionResolver(raw_options) : null;

		commandId = innerData.getString("id");
		commandName = innerData.getString("name");
		commandType = ApplicationCommand.Type.resolve(innerData.getLong("type"));
	}

	public CompletableFuture<Void> _reply(InteractionCallbackType type, InteractionReplyMessagePayload payload) {
		final var path = "/interactions/" + id + '/' + token + "/callback";
		return CompletableFuture.runAsync(() -> client.api.post(path, payload.toJSONString()));
	}

}
