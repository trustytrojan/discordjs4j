package discord.structures;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import simple_json.JSONObject;

public interface DiscordResource extends Identifiable {
	DiscordClient client();

	JSONObject getData();

	void setData(final JSONObject data);
	
	String apiPath();

	default CompletableFuture<Void> fetch() {
		return client().api.get(apiPath()).thenAcceptAsync((final var r) -> setData(r.toJSONObject()));
	}

	@Override
	default String id() {
		return getData().getString("id");
	}
}
