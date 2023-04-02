package discord.managers;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import discord.util.DiscordResourceMap;
import simple_json.JSON;
import simple_json.JSONObject;
import discord.client.DiscordClient;
import discord.structures.DiscordResource;

public abstract class DataManager<T extends DiscordResource> implements Iterable<T> {

	public final DiscordResourceMap<T> cache = new DiscordResourceMap<>();
	protected final DiscordClient client;

	protected DataManager(DiscordClient client) {
		this.client = client;
	}

	public Iterator<T> iterator() {
		return cache.iterator();
	}

	/**
	 * Constructs the {@code DiscordObject}, then caches it.
	 * 
	 * @param data The data to construct the {@code DiscordObject} with
	 * @return The cached {@code DiscordObject}
	 */
	public abstract T cache(JSONObject data);

	/**
	 * Caches {@code t}, then returns it.
	 * 
	 * @param t The {@code DiscordObject} to cache
	 * @return {@code t}
	 */
	public T cache(T t) {
		cache.put(t.id(), t);
		return t;
	}

	public T fetch(String id) {
		return fetch(id, false);
	}

	public CompletableFuture<T> fetchAsync(String id) {
		return CompletableFuture.supplyAsync(() -> fetch(id, false));
	}

	abstract public T fetch(String id, boolean force);

	public CompletableFuture<T> fetchAsync(String id, boolean force) {
		return CompletableFuture.supplyAsync(() -> fetch(id, force));
	}

	protected T fetch(String id, String path, boolean force) {
		if (!force) {
			final var cached = cache.get(id);
			if (cached != null) {
				return cached;
			}
		}
		final var data = JSON.parseObject(client.api.get(path));
		return cache(data);
	}

}
