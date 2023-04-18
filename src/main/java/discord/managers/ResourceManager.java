package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.util.IdMap;
import simple_json.JSONObject;
import discord.client.DiscordClient;
import discord.structures.DiscordResource;
import discord.structures.Identifiable;

public abstract class ResourceManager<T extends DiscordResource & Identifiable> {
	public final IdMap<T> cache = new IdMap<>();
	protected final DiscordClient client;

	protected ResourceManager(final DiscordClient client) {
		this.client = client;
	}

	public abstract T construct(final JSONObject data);

	protected String getIdFromData(final JSONObject data) {
		return data.getString("id");
	}

	// if this is called we know the cache WILL be modified
	public T cache(final JSONObject data) {
		final var cached = cache.get(getIdFromData(data));
		
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

	public CompletableFuture<T> fetch(final String id) {
		return fetch(id, false);
	}

	public abstract CompletableFuture<T> fetch(final String id, final boolean force);

	protected CompletableFuture<T> fetch(final String id, final String path, final boolean force) {
		if (!force) {
			final var cached = cache.get(id);
			if (cached != null) {
				return CompletableFuture.completedFuture(cached);
			}
		}
		return client.api.get(path).thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}
}
