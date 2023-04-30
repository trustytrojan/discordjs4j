package discord.util;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Supplier;

import discord.structures.Identifiable;

public class IdMap<V extends Identifiable> extends TreeMap<String, V> {
	private static final Random rand = new Random();
	private long sizeLimit = Long.MAX_VALUE;

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

	public void setSizeLimit(long sizeLimit) {
		if (sizeLimit < 50) {
			throw new IllegalArgumentException("Size limit too low! Must be >= 50");
		}

		this.sizeLimit = sizeLimit;
	}
	
	@Override
	public V put(String key, V value) {
		final var oldValue = super.put(key, value);

		if (size() > sizeLimit) {
			remove(firstKey());
		}

		return oldValue;
	}

	public IdMap<V> intersection(final IdMap<V> other) {
		final var intersection = new IdMap<V>();
		for (final var key : keySet()) {
			if (other.containsKey(key)) {
				intersection.put(key, get(key));
			}
		}
		return intersection;
	}
}
