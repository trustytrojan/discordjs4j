package discord.structures;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import sj.SjObject;

public interface DiscordResource {
	DiscordClient client();

	SjObject getData();

	void setData(SjObject data);

	String apiPath();

	default CompletableFuture<Void> fetch() {
		return client().api.get(apiPath()).thenAcceptAsync(r -> setData(r.toJsonObject()));
	}

	default String id() {
		return getData().getString("id");
	}
}
