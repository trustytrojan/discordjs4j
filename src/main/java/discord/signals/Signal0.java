package discord.signals;

import java.util.HashSet;

public class Signal0 {
	
	private final HashSet<Runnable> runnables = new HashSet<>();
	
	public void connect(Runnable r) {
		runnables.add(r);
	}

	public void disconnect(Runnable r) {
		runnables.remove(r);
	}
	
	public void emit() {
		for (final var r : runnables)
			r.run();
	}
	
}
