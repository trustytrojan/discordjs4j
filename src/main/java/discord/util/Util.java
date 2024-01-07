package discord.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
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
	public static <T> T printStackTrace(Throwable e) {
		e.printStackTrace();
		return null;
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

	private static final Pattern HEXADECIMAL_COLOR_CODE_REGEX = Pattern.compile("^#?[0-9a-fA-F]{6}$");

	public static int resolveHexColor(String hexColor) throws IllegalArgumentException {
		if (!HEXADECIMAL_COLOR_CODE_REGEX.matcher(hexColor).find())
			throw new IllegalArgumentException("Hex color string is not in correct format: #RRGGBB");
		return Integer.parseInt(hexColor, 16);
	}

	/**
	 * Returns the set difference of {@code c1} and {@code c2}, i.e., the elements that are in {@code c1} but not in {@code c2}.
	 * The function does not modify the input collections.
	 * The input collections are expected to represent sets; they should not contain duplicate elements.
	 *
	 * @param c1 the first collection
	 * @param c2 the second collection
	 * @return The set difference of {@code c1} and {@code c2}
	 */
	public static <T> Collection<T> setDifference(Collection<T> c1, Collection<T> c2) {
		final var difference = new HashSet<T>(c1);
		difference.removeAll(c2);
		return difference;
	}

	public static String encodeUrlParams(Map<String, String> params) {
		final var size = params.size();
		if (size == 0) return "";
		if (size == 1) {
			final var entry = params.entrySet().iterator().next();
			return '?' + entry.getKey() + '=' + entry.getValue();
		}
		final var sb = new StringBuilder("?");
		final var itr = params.entrySet().iterator();
		boolean hasNext;
		while (hasNext = itr.hasNext()) {
			final var entry = itr.next();
			sb.append(entry.getKey()).append('=').append(entry.getValue());
			if (hasNext) sb.append('&');
		}
		return sb.toString();
	}

	@SafeVarargs
	public static DiscordResource[] awaitResources(CompletableFuture<? extends DiscordResource>... resourceCfs) {
		return Stream.of(resourceCfs).map(CompletableFuture::join).toArray(DiscordResource[]::new);
	}
}
