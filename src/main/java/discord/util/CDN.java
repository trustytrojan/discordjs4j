package discord.util;

public final class CDN {
	private static final String BASE_URL = "https://cdn.discordapp.com";

	public static enum AllowedSize {
		_16(16), _32(32), _64(64), _128(128), _256(256), _512(512), _1024(1024), _2048(2048), _4096(4096);

		public final int value;

		private AllowedSize(int value) {
			this.value = value;
		}
	}

	public static enum AllowedExtension {
		WEBP("webp"), PNG("png"), JPG("jpg"), JPEG("jpeg"), GIF("gif");

		public final String value;

		private AllowedExtension(String value) {
			this.value = value;
		}
	}

	private static String makeURL(String path, AllowedSize size, AllowedExtension extension) {
		return BASE_URL + path + '.' + ((extension != null) ? extension.value : "webp") + ((size != null) ? ("?size=" + size.value) : "");
	}

	private static String dynamicMakeURL(String path, String hash, AllowedSize size, AllowedExtension extension) {
		if (hash.startsWith("a_"))
			return makeURL(path, size, AllowedExtension.GIF);
		return makeURL(path, size, extension);
	}

	public static String customEmoji(String emojiId, AllowedSize size, AllowedExtension extension) {
		return makeURL("/emojis/"+emojiId, size, extension);
	}

	public static String guildIcon(String guildId, String hash, AllowedSize size, AllowedExtension extension) {
		return dynamicMakeURL("/icons/"+guildId+'/'+hash, hash, size, extension);
	}

	public static String guildSplash(String guildId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/splashes/" + guildId + '/' + hash, size, extension);
	}

	public static String guildDiscoverySplash(String guildId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/discovery-splashes/" + guildId + '/' + hash, size, extension);
	}

	public static String guildOrUserBanner(String guildOrUserId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/banners/" + guildOrUserId + '/' + hash, size, extension);
	}

	public static String defaultUserAvatar(short userDiscriminator) {
		return makeURL("/embed/avatars/" + (userDiscriminator % 5), null, AllowedExtension.PNG);
	}

	public static String userAvatar(String userId, String hash, AllowedSize size, AllowedExtension extension) {
		return dynamicMakeURL("/avatars/" + userId + '/' + hash, hash, size, extension);
	}

	public static String guildMemberAvatar(String guildId, String userId, String hash, AllowedSize size, AllowedExtension extension) {
		return dynamicMakeURL("/guilds/" + guildId + "/users/" + userId + '/' + hash, hash, size, extension);
	}

	public static String applicationIcon(String applicationId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/app-icons/" + applicationId + '/' + hash, size, extension);
	}

	public static String roleIcon(String roleId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/role-icons/" + roleId + '/' + hash, size, extension);
	}

	public static String channelIcon(String groupDmId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/channel-icons/" + groupDmId + '/' + hash, size, extension);
	}

	public static interface URLFactory {
		String hash();
		String url(AllowedSize size, AllowedExtension extension);

		default String url(AllowedSize size) {
			return url(size, null);
		}

		default String url(AllowedExtension extension) {
			return url(null, extension);
		}

		default String url() {
			return url(null, null);
		}
	}
}
