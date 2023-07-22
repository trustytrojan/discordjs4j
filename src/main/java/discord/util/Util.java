package discord.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public final class Util {
	public static final Runnable NO_OP = () -> {};

	/**
	 * Defined only to be used as a method reference for
	 * {@link CompletableFuture#exceptionally}.
	 */
	public static <T> T printStackTrace(Throwable e) {
		e.printStackTrace();
		return null;
	}

	public static void repeat(Runnable r, long ms) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				r.run();
			}
		}, 0, ms);
	}

	public static boolean fileExists(String path) {
		return Files.exists(Path.of(path));
	}

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

	private static final Pattern HEXADECIMAL_COLOR_CODE_REGEX = Pattern.compile("^#?[0-9a-fA-F]{6}$");

	public static int resolveHexColor(String hexColor) throws IllegalArgumentException {
		if (!HEXADECIMAL_COLOR_CODE_REGEX.matcher(hexColor).find())
			throw new IllegalArgumentException("Hex color string is not in correct format: #RRGGBB");
		return Integer.parseInt(hexColor, 16);
	}

	public static <T> Collection<T> setDifference(Collection<T> c1, Collection<T> c2) {
		final var difference = new HashSet<T>(c1);
		difference.removeAll(c2);
		return difference;
	}

	public static void main(String[] args) {
		System.out.println(setDifference(Set.of(1, 3, 5), Set.of(5, 2, 6, 3)));
	}
}
