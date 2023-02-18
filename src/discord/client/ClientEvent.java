package discord.client;

import java.util.HashSet;
import java.util.function.Consumer;

public class ClientEvent<T> {
	
	private final HashSet<Runnable> runnables = new HashSet<>();
	private final HashSet<Consumer<T>> consumers = new HashSet<>();
	
	public void connect(Runnable r) { runnables.add(r); }
	public void connect(Consumer<T> c) { consumers.add(c); }

	public void disconnect(Runnable r) { runnables.remove(r); }
	public void disconnect(Consumer<T> c) { consumers.remove(c); }

	public void emit() {
		emit(null);
	}

	public void emit(T t) {
		for (final var r : runnables) r.run();
		for (final var c : consumers) c.accept(t);
	}

}
