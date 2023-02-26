package discord.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

public class BetterMap<K, V> extends TreeMap<K, V> implements Iterable<V> {

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

}
