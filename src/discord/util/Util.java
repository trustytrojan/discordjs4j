package discord.util;

import java.io.FileInputStream;
//import java.time.Instant;
//import java.util.Date;

public final class Util {

	public static final String readFile(String filePath) throws Exception {
		final var stream = new FileInputStream(filePath);
		final var token = new String(stream.readAllBytes());
		stream.close();
		return token;
	}

	// public static Date long_to_date(long ms) {
	//   return Date.from(Instant.ofEpochMilli(ms));
	// }

}
