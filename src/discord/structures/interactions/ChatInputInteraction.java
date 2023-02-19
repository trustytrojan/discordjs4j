package discord.structures.interactions;

import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONObject;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.enums.CommandType;
import discord.enums.InteractionCallbackType;
import discord.structures.MessagePayload;

public class ChatInputInteraction extends Interaction implements RepliableInteraction {

	public final ChatInputInteractionOptions options;

	public ChatInputInteraction(DiscordClient client, BetterJSONObject data) {
		super(client, data);
		options = new ChatInputInteractionOptions(innerData().getObjectArray("options"));
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
	public CompletableFuture<Void> _reply(InteractionCallbackType type, MessagePayload payload) {
		final var path = String.format("/interactions/%s/%s/callback", id(), token());
		final var obj = new JSONObject();
		obj.put("type", type.value);
		if (payload != null) obj.put("data", payload.toJSONObject());
		return CompletableFuture.runAsync(() -> {
			try {
				client.api.post(path, obj.toJSONString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
