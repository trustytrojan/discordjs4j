package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.util.IdMap;
import simple_json.JSONObject;
import discord.client.DiscordClient;
import discord.structures.DiscordResource;

public abstract class ResourceManager<T extends DiscordResource> {
	public final IdMap<T> cache = new IdMap<>();
	protected final DiscordClient client;

	protected ResourceManager(DiscordClient client) {
		this.client = client;
	}

	public abstract T construct(JSONObject data);

	public T cache(JSONObject data) {
		final var cached = cache.get(data.getString("id"));
		
		// if not already cached, construct new object
		if (cached == null) {
			final var constructed = construct(data);
			cache.put(constructed);
			return constructed;
		}

		// if an object is cached, just set its data
		cached.setData(data);
		return cached;
	}

	public CompletableFuture<T> fetch(String id) {
		return fetch(id, false);
	}

	public abstract CompletableFuture<T> fetch(String id, boolean force);

	protected CompletableFuture<T> fetch(String id, String path, boolean force) {
		return CompletableFuture.supplyAsync(() -> {
			if (!force) {
				final var cached = cache.get(id);
				if (cached != null) {
					return cached;
				}
			}
			return cache(client.api.get(path).join().toJSONObject());
		});
	}
}
