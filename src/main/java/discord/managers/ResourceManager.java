package discord.managers;

import java.util.Iterator;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import discord.client.APIClient.JsonResponse;
import discord.client.DiscordClient;
import discord.resources.DiscordResource;
import discord.util.Util;
import sj.SjObject;

public abstract class ResourceManager<T extends DiscordResource> implements Iterable<T> {
	public final TreeMap<String, T> cache = new TreeMap<>();
	protected final DiscordClient client;
	protected final String basePath;

	protected ResourceManager(DiscordClient client, String basePath) {
		this.client = Objects.requireNonNull(client);
		this.basePath = basePath;
	}

	protected String pathWithId(String id) {
		return basePath + '/' + id;
	}

	@Override
	public Iterator<T> iterator() {
		return cache.values().iterator();
	}

	protected abstract T construct(SjObject data);

	// cache an already constructed object
	public T cache(T resource) {
		cache.put(resource.id(), resource);
		return resource;
	}

	// if this is called we know the cache WILL be modified
	public T cache(SjObject data) {
		final var id = data.getString("id");
		if (id == null) throw new RuntimeException("JSON object has no \"id\" key! " + data);
		final var cached = cache.get(id);
		if (cached == null) return cache(construct(data));
		cached.setData(data);
		return cached;
	}

	public void cacheNewDeleteOld(JsonResponse r) {
		final var freshIds = r.toJsonObjectArray().stream().map(this::cache).map(DiscordResource::id).toList();
		final var deletedIds = Util.setDifference(cache.keySet(), freshIds);
		deletedIds.forEach(cache::remove);
	}

	public CompletableFuture<T> get(String id) {
		return get(id, false);
	}

	/**
	 * Gets the resource identified by {@code id} from the Discord API, or the cache
	 * if available. If {@code force} is true, the cache check will be ignored.
	 * 
	 * @param id    Resource identifier
	 * @param force Whether to skip the cache check
	 * @return A {@code CompletableFuture} of the resource
	 */
	public CompletableFuture<T> get(String id, boolean force) {
		Objects.requireNonNull(id);
		if (!force) {
			final var cached = cache.get(id);
			if (cached != null)
				return CompletableFuture.completedFuture(cached);
		}
		return client.api.get(pathWithId(id)).thenApply(r -> cache(r.toJsonObject()));
	}

	public abstract CompletableFuture<Void> refreshCache();
}
