package discord.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class Util {
	public static final Runnable DO_NOTHING = () -> {};

	public static final Function<Throwable, ? extends Void> PRINT_STACK_TRACE =
			(final var e) -> {
				e.printStackTrace();
				return null;
			};

	public static String readFile(String path) {
		try {
			return Files.readString(Path.of(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeFile(String path, String data) {
		try {
			Files.writeString(Path.of(path), data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Date longToDate(long ms) {
		return Date.from(Instant.ofEpochMilli(ms));
	}

	public static int resolveColor(String hexColor) throws IllegalArgumentException {
		final var regex = Pattern.compile("^#?[0-9a-fA-F]{6}$", Pattern.CASE_INSENSITIVE);
		if (!regex.matcher(hexColor).find())
			throw new IllegalArgumentException("Hex color string is not in correct format");
		return Integer.parseInt(hexColor, 16);
	}
}
