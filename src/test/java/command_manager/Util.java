package command_manager;

import java.util.Enumeration;
import java.util.Iterator;

public class Util {
	static <T> Iterable<T> iterableFrom(Iterator<T> iterator) {
		return new Iterable<>() {
			@Override
			public Iterator<T> iterator() {
				return new Iterator<>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public T next() {
						return iterator.next();
					}
				};
			}
		};
	}

	static <T> Iterable<T> IterableFrom(Enumeration<T> enumeration) {
		return iterableFrom(enumeration.asIterator());
	}
}
