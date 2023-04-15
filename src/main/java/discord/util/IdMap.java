package discord.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Supplier;

import discord.structures.Identifiable;

public class IdMap<V extends Identifiable> extends TreeMap<String, V> implements Iterable<V> {
	private static final Random rand = new Random();

	@Override
	public Iterator<V> iterator() {
		return values().iterator();
	}

	public V random() {
		final var array = new ArrayList<V>(values());
		return array.get(rand.nextInt(array.size()));
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

	public V put(V value) {
		return put(value.id(), value);
	}

	@Override
	public V put(String key, V value) {
		final var oldValue = super.put(key, value);
		if (size() > 50) {
			remove(firstKey());
		}
		return oldValue;
	}
}
