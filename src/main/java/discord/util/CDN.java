package discord.util;

import java.util.Arrays;
import java.util.List;

public final class CDN {

	private static final String base_url = "https://cdn.discordapp.com";
	private static final List<Integer> allowed_sizes = Arrays.asList(16, 32, 64, 128, 256, 512, 1024, 2048, 4096);
	private static final List<String> allowed_extensions = Arrays.asList("webp", "png", "jpg", "jpeg", "gif");

	private static String makeURL(String path, int size, String extension) {
		if (extension == null)
			extension = "webp";
		else if (!allowed_extensions.contains(extension))
			throw new RuntimeException("Invalid extension provided: " + extension + "\nMust be one of: " + allowed_extensions);
		if (size > 0 && !allowed_sizes.contains(size))
			throw new RuntimeException("Invalid size provided: " + size + "\nMust be one of: " + allowed_sizes.toString());
		var url = base_url + path + '.' + extension;
		if (size > 0)
			url += ("?size=" + size);
		return url;
	}

	private static String dynamicMakeURL(String path, String hash, int size, String extension) {
		if (hash.startsWith("a_"))
			return makeURL(path, size, "gif");
		return makeURL(path, size, extension);
	}

	public static String customEmoji(String emojiId, int size, String extension) {
		return makeURL("/emojis/"+emojiId, size, extension);
	}

	public static String guildIcon(String guildId, String hash, int size, String extension) {
		return dynamicMakeURL("/icons/"+guildId+'/'+hash, hash, size, extension);
	}

	public static String guildSplash(String guildId, String hash, int size, String extension) {
		return makeURL("/splashes/"+guildId+'/'+hash, size, extension);
	}

	public static String guildDiscoverySplash(String guildId, String hash, int size, String extension) {
		return makeURL("/discovery-splashes/"+guildId+'/'+hash, size, extension);
	}

	public static String guildOrUserBanner(String guildOrUserId, String hash, int size, String extension) {
		return makeURL("/banners/"+guildOrUserId+'/'+hash, size, extension);
	}

	public static String defaultUserAvatar(short userDiscriminator) {
		return makeURL("/embed/avatars/"+(userDiscriminator % 5), 0, "png");
	}

	public static String userAvatar(String userId, String hash, int size, String extension) {
		return dynamicMakeURL("/avatars/"+userId+'/'+hash, hash, size, extension);
	}

	public static String guildMemberAvatar(String guildId, String userId, String hash, int size, String extension) {
		return dynamicMakeURL("/guilds/"+guildId+"/users/"+userId+'/'+hash, hash, size, extension);
	}

	public static String roleIcon(String roleId, String hash, int size, String extension) {
		return makeURL("/role-icons/"+roleId+'/'+hash+".png", size, extension);
	}

	public static interface URLFactory {
		public String hash();
		public String url(int size, String extension);

		default String url(int size) {
			return url(size, null);
		}

		default String url(String extension) {
			return url(0, extension);
		}

		default String url() {
			return url(0, null);
		}
	}

}
