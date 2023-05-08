package discord.util;

import java.util.Timer;
import java.util.TimerTask;

public class RunnableRepeater extends Timer {
	public void repeat(Runnable r, long ms) {
		schedule(new TimerTask() {
			public void run() {
				r.run();
			}
		}, 0, ms);
	}
}
