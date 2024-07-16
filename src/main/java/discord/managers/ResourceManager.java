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

	/**
	 * @return An {@link Iterator} that iterates over the resources in {@link #cache}.
	 */
	@Override
	public Iterator<T> iterator() {
		return cache.values().iterator();
	}

	/**
	 * This method should construct an object of type {@link T} to be cached in {@link #cache}.
	 * @param data JSON object representing the type {@link T} to cache from Discord
	 * @return The constructed resource object
	 */
	protected abstract T construct(final SjObject data);

	/**
	 * Cache an already constructed resource object.
	 * @param resource Resource object
	 * @return The same object
	 */
	protected T cache(final T resource) {
		cache.put(resource.getId(), resource);
		return resource;
	}

	/**
	 * Cache data in a JSON object received from Discord. 
	 * If the resource isn't already cached, passes {@code data} to {@link #construct} and caches the returned object in {@link #cache}.
	 * Otherwise, calls {@link DiscordResource#setData} to update the object's data with {@code data}.
	 * @param data JSON object representing the type {@link T} to cache from Discord
	 * @return The constructed resource object
	 */
	public T cache(final SjObject data) {
		final var cached = cache.get(Objects.requireNonNull(data.getString("id")));

		if (cached == null)
			// create resource
			return cache(construct(data));

		// update resource
		cached.setData(data);
		return cached;
	}

	/**
	 * For Discord API endpoints that only serve resources in bulk (guild member roles, relationships, etc.),
	 * this method must be called to perform a set difference between new and old objects to remove any deleted
	 * resources from the {@link #cache}.
	 * @param resp Bulk resource JSON response from Discord
	 */
	protected void cacheNewDeleteOld(final JsonResponse resp) {
		final var oldIds = new HashSet<>(cache.keySet());
		final var newIds = resp.asObjectArray().stream().map(this::cache).map(DiscordResource::getId).toList();
		final var deletedIds = Util.setDifference(oldIds, newIds);
		deletedIds.forEach(cache::remove);
	}

	/**
	 * Alias for {@link #get(String, boolean)} that prefers the cache.
	 * @param id Discord resource ID
	 * @return A {@link CompletableFuture} that resolves to the resource
	 */
	public CompletableFuture<T> get(final String id) {
		return get(id, false);
	}

	/**
	 * Gets the resource identified by {@code id} from the Discord API, or the cache
	 * if available. If {@code force} is true, skips the cache check.
	 * @param id Resource identifier
	 * @param force Whether to skip the cache check
	 * @return A {@link CompletableFuture} that resolves to the resource
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

	/**
	 * This method should refresh the {@link #cache} by calling the Discord API endpoint
	 * configured in the subclass of {@link ResourceManager} (stored in {@link #basePath}).
	 * It should then update the {@link #cache} as necessary.
	 */
	public abstract CompletableFuture<Void> refreshCache();
}
