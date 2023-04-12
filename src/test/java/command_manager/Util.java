package command_manager;

import java.util.Enumeration;
import java.util.Iterator;

final class Util {
	static <T> Iterable<T> iterableFrom(final Iterator<T> iterator) {
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

	static <T> Iterable<T> iterableFrom(final Enumeration<T> enumeration) {
		return iterableFrom(enumeration.asIterator());
	}
}
