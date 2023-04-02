package discord.structures.interactions;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.enums.CommandType;
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

	public CommandType commandType() {
		return CommandType.get(innerData().getLong("type"));
	}

	@SuppressWarnings("unchecked")
	public CompletableFuture<Void> _reply(InteractionCallbackType type, InteractionReplyMessagePayload payload) {
		final var path = "/interactions/" + id() + '/' + token() + "/callback";
		final var obj = new JSONObject();
		obj.put("type", type.value);
		if (payload != null)
			obj.put("data", payload.toJSONObject());
		return CompletableFuture.runAsync(() -> {
			try {
				client.api.post(path, obj.toJSONString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
