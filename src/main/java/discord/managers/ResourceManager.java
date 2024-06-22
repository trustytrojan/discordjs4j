package discord.managers;

import java.util.HashSet;
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

	protected ResourceManager(final DiscordClient client, final String basePath) {
		this.client = Objects.requireNonNull(client);
		this.basePath = basePath;
	}

	protected String pathWithId(final String id) {
		return basePath + '/' + id;
	}

	@Override
	public Iterator<T> iterator() {
		return cache.values().iterator();
	}

	protected abstract T construct(final SjObject data);

	// cache an already constructed object
	protected T cache(final T resource) {
		cache.put(resource.getId(), resource);
		return resource;
	}

	// if this is called we know the cache WILL be modified
	public T cache(final SjObject data) {
		final var id = data.getString("id");
		if (id == null)
			// should never happen; otherwise discord is broken
			throw new RuntimeException("JSON object has no \"id\" key! " + data);

		final var cached = cache.get(id);
		if (cached == null)
			// create resource
			return cache(construct(data));

		// update resource
		cached.setData(data);
		return cached;
	}

	protected void cacheNewDeleteOld(final JsonResponse r) {
		final var oldIds = new HashSet<>(cache.keySet());
		final var newIds = r.asObjectArray().stream().map(this::cache).map(DiscordResource::getId).toList();
		final var deletedIds = Util.setDifference(oldIds, newIds);
		deletedIds.forEach(cache::remove);
	}

	public CompletableFuture<T> get(final String id) {
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
	public CompletableFuture<T> get(final String id, final boolean force) {
		Objects.requireNonNull(id);
		if (!force) {
			final var cached = cache.get(id);
			if (cached != null)
				return CompletableFuture.completedFuture(cached);
		}
		return client.api.get(pathWithId(id)).thenApply(r -> cache(r.asObject()));
	}

	public abstract CompletableFuture<Void> refreshCache();
}
