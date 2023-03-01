package discord.util;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.regex.Pattern;

public final class Util {

	public static String readFile(String filePath) throws IOException {
		final var stream = new FileInputStream(filePath);
		final var data = new String(stream.readAllBytes());
		stream.close();
		return data;
	}

	public static void writeFile(String filePath, String data) throws IOException {
		final var writer = new FileWriter(filePath);
		writer.write(data);
		writer.close();
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
