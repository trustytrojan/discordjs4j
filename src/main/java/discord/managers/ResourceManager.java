package discord.managers;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.util.IdMap;
import simple_json.SjObject;
import discord.client.DiscordClient;
import discord.structures.DiscordResource;
import discord.structures.Identifiable;

public abstract class ResourceManager<T extends DiscordResource & Identifiable> implements Iterable<T> {
	public final IdMap<T> cache = new IdMap<>();
	protected final DiscordClient client;

	protected ResourceManager(DiscordClient client) {
		this.client = Objects.requireNonNull(client);
	}

	@Override
	public Iterator<T> iterator() {
		return cache.values().iterator();
	}

	public abstract T construct(SjObject data);

	// cache an already constructed object
	public T cache(T resource) {
		Objects.requireNonNull(resource);
		cache.put(resource.id(), resource);
		return resource;
	}

	// if this is called we know the cache WILL be modified
	public T cache(SjObject data) {
		Objects.requireNonNull(data);
		final var cached = cache.get(data.getString("id"));
		
		// if not already cached, construct new object
		if (cached == null) {
			return cache(construct(data));
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
		Objects.requireNonNull(id);
		Objects.requireNonNull(path);

		if (!force) {
			final var cached = cache.get(id);
			
			if (cached != null) {
				return CompletableFuture.completedFuture(cached);
			}
		}

		return client.api.get(path)
			.thenApplyAsync(r -> cache(r.toJsonObject()));
	}

	public abstract CompletableFuture<Void> refreshCache();
}
