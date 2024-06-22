package discord.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	public static void log(final String message) {
		final var callerClassName = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(frames ->
			frames.skip(1) // Skip StackWalker's own frame
				.findFirst()
				.map(frame -> frame.getClassName())
				.orElse("?"));
		System.out.println('[' + LocalDateTime.now().format(dateTimeFormatter) + ' ' + callerClassName + "] " + message);
	}
}
