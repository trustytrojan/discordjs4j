package discord.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import discord.resources.DiscordResource;

public final class Util {
	public static final Runnable NO_OP = () -> {};

	/**
	 * Defined only to be used as a method reference for
	 * {@link CompletableFuture#exceptionally}.
	 */
	public static <T> T printStackTrace(final Throwable e) {
		e.printStackTrace();
		return null;
	}

	public static class File {
		public static boolean exists(final String path) {
			return Files.exists(Path.of(path));
		}

		public static String read(final String path) throws IOException {
			return Files.readString(Path.of(path));
		}

		public static void write(final String path, final String data) throws IOException {
			Files.writeString(Path.of(path), data);
		}

		private File() {}
	}

	private static final Pattern HEXADECIMAL_COLOR_CODE_REGEX = Pattern.compile("^#?[0-9a-fA-F]{6}$");

	public static int resolveHexColor(final String hexColor) throws IllegalArgumentException {
		if (!HEXADECIMAL_COLOR_CODE_REGEX.matcher(hexColor).find())
			throw new IllegalArgumentException("Hex color string is not in correct format: #RRGGBB");
		return Integer.parseInt(hexColor, 16);
	}

	public static <T> Collection<T> setDifference(final Collection<T> c1, final Collection<T> c2) {
		final var difference = new HashSet<>(c1);
		difference.removeAll(c2);
		return difference;
	}

	@SafeVarargs
	public static DiscordResource[] awaitResources(final CompletableFuture<? extends DiscordResource>... resourceCfs) {
		return Stream.of(resourceCfs).map(CompletableFuture::join).toArray(DiscordResource[]::new);
	}

	public static class Snowflake {
		private static final long DISCORD_EPOCH = 1420070400000L;

		public static Instant toInstant(final String snowflake) {
			return Instant.ofEpochMilli((Long.parseLong(snowflake) >> 22) + DISCORD_EPOCH);
		}

		public static String fromInstant(final Instant instant) {
			return String.valueOf((instant.toEpochMilli() - DISCORD_EPOCH) << 22);
		}

		private Snowflake() {}
	}

	private Util() {}
}
