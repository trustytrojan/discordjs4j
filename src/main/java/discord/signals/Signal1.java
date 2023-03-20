package discord.signals;

import java.util.HashSet;
import java.util.function.Consumer;

public class Signal1<T> {

	private final HashSet<Consumer<T>> consumers = new HashSet<>();

	public void connect(Consumer<T> c) {
		consumers.add(c);
	}

	public void disconnect(Consumer<T> c) {
		consumers.remove(c);
	}

	public void emit(T t) {
		for (final var c : consumers)
			c.accept(t);
	}

}
