package discord.util;

import java.util.Timer;
import java.util.TimerTask;

public class RunnableRepeater extends Timer {

	/**
	 * A wrapper for Timer.schedule that accepts a Runnable
	 * and repeats it once every repeat_delay_ms.
	 * 
	 * @param r
	 *            The Runnable to run
	 * @param ms
	 *            The time in milliseconds to wait before
	 *            running again
	 */
	public void repeat(Runnable r, long ms) {
		schedule(new TimerTask() {
			public void run() {
				r.run();
			}
		}, 0, ms);
	}

}
