package discord.managers;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.util.IdMap;
import simple_json.JSONObject;
import discord.client.DiscordClient;
import discord.structures.DiscordResource;
import discord.structures.Identifiable;

public abstract class ResourceManager<T extends DiscordResource & Identifiable> implements Iterable<T> {
	public final IdMap<T> cache = new IdMap<>();
	protected final DiscordClient client;

	protected ResourceManager(final DiscordClient client) {
		this.client = Objects.requireNonNull(client);
	}

	@Override
	public Iterator<T> iterator() {
		return cache.values().iterator();
	}

	public abstract T construct(final JSONObject data);

	protected String getIdFromData(final JSONObject data) {
		return data.getString("id");
	}

	// cache an already constructed object
	public T cache(final T resource) {
		cache.put(resource.id(), resource);
		return resource;
	}

	// if this is called we know the cache WILL be modified
	public T cache(final JSONObject data) {
		final var cached = cache.get(getIdFromData(data));
		
		// if not already cached, construct new object
		if (cached == null) {
			return cache(construct(data));
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

	public abstract CompletableFuture<Void> refreshCache();
}
