package discord.managers;

import java.util.Iterator;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.DiscordResource;
import sj.SjObject;

public abstract class ResourceManager<T extends DiscordResource> implements Iterable<T> {
	public final TreeMap<String, T> cache = new TreeMap<>();
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
		cache.put(resource.id(), resource);
		return resource;
	}

	// if this is called we know the cache WILL be modified
	public T cache(SjObject data) {
		if (data.getString("id") == null)
			System.out.println(data);

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

		return client.api.get(path).thenApply(r -> cache(r.toJsonObject()));
	}

	public abstract CompletableFuture<Void> refreshCache();
}
