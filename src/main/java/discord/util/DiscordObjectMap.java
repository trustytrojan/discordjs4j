package discord.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.function.Supplier;

import discord.structures.DiscordObject;

public class DiscordObjectMap<V extends DiscordObject> extends TreeMap<String, V> implements Iterable<V> {

	@Override
	public Iterator<V> iterator() {
		return values().iterator();
	}

	public V random() {
		final var array = new ArrayList<V>(values());
		Collections.shuffle(array);
		return array.get(0);
	}

	public V first() {
		return firstEntry().getValue();
	}

	public V ensure(String key, Supplier<V> supplier) {
		final var existingValue = get(key);
		final var defaultValue = supplier.get();
		
		if (existingValue == null) {
			put(key, defaultValue);
			return defaultValue;
		} else {
			return existingValue;
		}
	}

}
