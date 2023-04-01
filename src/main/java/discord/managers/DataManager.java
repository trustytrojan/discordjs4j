package discord.managers;

import java.util.Iterator;

import discord.util.DiscordObjectMap;
import simple_json.JSON;
import simple_json.JSONObject;
import discord.client.DiscordClient;
import discord.structures.DiscordObject;

public abstract class DataManager<T extends DiscordObject> implements Iterable<T> {

	protected final DiscordObjectMap<T> cache = new DiscordObjectMap<>();
	protected final DiscordClient client;

	protected DataManager(DiscordClient client) {
		this.client = client;
	}

	public Iterator<T> iterator() {
		return cache.iterator();
	}

	protected abstract T cacheNew(JSONObject data);

	/**
	 * If an object has already been constructed and needs
	 * to be cached, call this method.
	 * 
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
	 * 
	 * @param data data from the Discord API
	 * @return the object
	 */
	public T cacheData(JSONObject data) {
		final var fromCache = cache.get(data.getString("id"));
		if (fromCache == null)
			return cacheNew(data);
		fromCache.setData(data);
		return fromCache;
	}

	/**
	 * This method calls {@code fetch(id, false)}.
	 * 
	 * @param id The ID of the resource to fetch from Discord.
	 * @return The resource object of the subclass's type. The cache method
	 *         is called to create the object and cache it.
	 */
	public T fetch(String id) {
		return fetch(id, false);
	}

	/**
	 * This method should call {@code fetch(String id, String path, boolean force)}.
	 * 
	 * @param id    The ID of the resource to fetch from Discord.
	 * @param force Whether to skip the cache check
	 * @return The resource object of the subclass's type. The cache method
	 *         is called to create the object and cache it.
	 */
	public abstract T fetch(String id, boolean force);

	/**
	 * Fetch a resource from Discord. This method should be called by the
	 * subclass when implementing the above two versions of fetch().
	 * 
	 * @param id    The ID of the resource to fetch from Discord.
	 * @param path  The API path to request. This should be provided by the
	 *              subclass.
	 * @param force Whether to skip the cache check.
	 * @return The resource object of the subclass's type. The cache method
	 *         is called to create the object and cache it.
	 */
	protected T fetch(String id, String path, boolean force) {
		if (!force) {
			final var cached = cache.get(id);
			if (cached != null)
				return cached;
		}
		final var data = JSON.parseObject(client.api.get(path));
		return cacheData(data);
	}

}
