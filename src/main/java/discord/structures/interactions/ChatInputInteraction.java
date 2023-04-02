package discord.structures.interactions;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Application.Command;
import discord.enums.InteractionCallbackType;
import discord.structures.payloads.InteractionReplyMessagePayload;
import simple_json.JSONObject;

public class ChatInputInteraction extends Interaction implements RepliableInteraction {

	public final ChatInputInteractionOptionResolver options;

	public ChatInputInteraction(DiscordClient client, JSONObject data) {
		super(client, data);
		final var raw_options = innerData().getObjectArray("options");
		ChatInputInteractionOptionResolver options = null;
		if (raw_options != null)
			options = new ChatInputInteractionOptionResolver(raw_options);
		this.options = options;
	}

	public String commandId() {
		return innerData().getString("id");
	}

	public String commandName() {
		return innerData().getString("name");
	}

	public Command.Type commandType() {
		return Command.Type.resolve(innerData().getLong("type"));
	}

	public CompletableFuture<Void> _reply(InteractionCallbackType type, InteractionReplyMessagePayload payload) {
		final var path = "/interactions/" + id() + '/' + token() + "/callback";
		final var obj = new JSONObject();

		obj.put("type", type.value);

		if (payload != null) {
			obj.put("data", payload);
		}

		return CompletableFuture.runAsync(() -> client.api.post(path, obj.toString()));
	}

}
