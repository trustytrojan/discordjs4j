package discord.managers;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import discord.util.IdMap;
import simple_json.JSONObject;
import discord.client.DiscordClient;
import discord.structures.DiscordResource;

public abstract class DataManager<T extends DiscordResource> implements Iterable<T> {

	public final IdMap<T> cache = new IdMap<>();
	protected final DiscordClient client;

	protected DataManager(DiscordClient client) {
		this.client = client;
	}

	public Iterator<T> iterator() {
		return cache.iterator();
	}

	public abstract T construct(JSONObject data);

	public T cache(JSONObject data) {
		final var cached = cache.get(data.getString("id"));
		
		if (cached == null) {
			final var constructed = construct(data);
			cache.put(constructed);
			return constructed;
		}

		cached.setData(data);
		return cached;
	}

	public T fetch(String id) {
		return fetch(id, false);
	}

	public CompletableFuture<T> fetchAsync(String id) {
		return CompletableFuture.supplyAsync(() -> fetch(id, false));
	}

	public abstract T fetch(String id, boolean force);

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
		return cache(client.api.get(path).toJSONObject());
	}

}
