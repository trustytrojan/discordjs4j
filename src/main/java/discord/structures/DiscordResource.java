package discord.structures;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import simple_json.SjObject;

public interface DiscordResource extends Identifiable {
	DiscordClient client();

	SjObject getData();

	void setData(final SjObject data);

	String apiPath();

	default CompletableFuture<Void> fetch() {
		return client().api.get(apiPath()).thenAcceptAsync(r -> setData(r.toJsonObject()));
	}

	@Override
	default String id() {
		return getData().getString("id");
	}
}
