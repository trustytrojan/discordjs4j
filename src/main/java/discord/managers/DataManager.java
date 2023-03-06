package discord.managers;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.util.BetterMap;
import discord.client.DiscordClient;
import discord.structures.DiscordObject;
import discord.util.JSON;

public abstract class DataManager<T extends DiscordObject> implements Iterable<T> {

	public final BetterMap<String, T> cache = new BetterMap<String, T>();
	protected final DiscordClient client;

	protected DataManager(DiscordClient client) {
		this.client = client;
	}

	public Iterator<T> iterator() {
		return cache.iterator();
	}

	/**
	 * Creates a new object for this data, caches it,
	 * and returns it. This is called {@code forceCache}
	 * because it skips the cache check.
	 * @param data data from the Discord API
	 * @return the object
	 */
	protected abstract T forceCache(BetterJSONObject data);

	/**
	 * If an object has already been constructed and needs
	 * to be cached, call this method.
	 * @param t The object to cache
	 * @return The same object
	 */
	public T cache(T t) {
		cache.put(t.id(), t);
		return t;
	}

	/**
	 * If the object that this data references is cached,
	 * give this data to that object. If not, {@code forceCache}
	 * is called.
	 * @param data data from the Discord API
	 * @return the object
	 */
	public T cache(BetterJSONObject data) {
		final var fromCache = cache.get(data.getString("id"));
		if (fromCache == null) return forceCache(data);
		fromCache.setData(data);
		return fromCache;
	}

	/**
	 * The fetch methods available to the user that hide the path requirement.
	 */
	public CompletableFuture<T> fetch(String id) {
		return fetch(id, false);
	}

	/**
	 * The fetch methods available to the user that hide the path requirement.
	 */
	public abstract CompletableFuture<T> fetch(String id, boolean force);

	/**
	 * Fetch a resource from Discord. This method should be called by the
	 * subclass when implementing the above two versions of fetch().
	 * 
	 * @param id The ID of the resource to fetch from Discord.
	 * @param path The API path to request. This should be provided by the subclass.
	 * @param force Whether to skip the cache check.
	 * @return The resource object of the subclass's type. The cache method
	 * is called to create the object and cache it.
	 */
	protected CompletableFuture<T> fetch(String id, String path, boolean force) {
		return CompletableFuture.supplyAsync(() -> {
			if (!force) {
				final var cached = cache.get(id);
				if (cached != null) return cached;
			}

			try {
				final var data = JSON.parseObject(client.api.get(path));
				return cache(data);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

}
