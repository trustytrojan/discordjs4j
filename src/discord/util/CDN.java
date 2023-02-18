package discord.util;

import java.util.Arrays;
import java.util.List;

public final class CDN {
	
	private static final String base_url = "https://cdn.discordapp.com";
	private static final List<Integer> allowed_sizes = Arrays.asList(16, 32, 64, 128, 256, 512, 1024, 2048, 4096);
	private static final List<String> allowed_extensions = Arrays.asList("webp", "png", "jpg", "jpeg", "gif");

	private static final String makeURL(String path, int size, String extension) {
		if (extension == null)
			extension = "webp";
		else if (!allowed_extensions.contains(extension))
			throw new RuntimeException("Invalid extension provided: " + extension + "\nMust be one of: " + allowed_extensions);
		if (size > 0 && !allowed_sizes.contains(size))
			throw new RuntimeException("Invalid size provided: " + size + "\nMust be one of: " + allowed_sizes.toString());
		var url = base_url + path + '.' + extension;
		if (size > 0) url += ("?size=" + size);
		return url;
	}

	private static final String dynamicMakeURL(String path, String hash, int size, String extension) {
		if (hash.startsWith("a_"))
			return makeURL(path, size, "gif");
		return makeURL(path, size, extension);
	}

	public static final String avatar(String id, String hash, int size, String extension) {
		return dynamicMakeURL("/avatars/"+id+'/'+hash, hash, size, extension);
	}

	public static final String icon(String id, String hash, int size, String extension) {
		return dynamicMakeURL("/icons/"+id+'/'+hash, hash, size, extension);
	}

	public static final String splash(String guild_id, String hash, int size, String extension) {
		return makeURL("/discovery-splashes/"+guild_id+'/'+hash, size, extension);
	}

}
