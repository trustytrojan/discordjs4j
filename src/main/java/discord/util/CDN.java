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
		return hash.startsWith("a_")
			? makeURL(path, size, AllowedExtension.GIF)
			: makeURL(path, size, extension);
	}

	public static String makeCustomEmojiURL(String emojiId, AllowedSize size, AllowedExtension extension) {
		return makeURL("/emojis/" + emojiId, size, extension);
	}

	public static String makeGuildIconURL(String guildId, String hash, AllowedSize size, AllowedExtension extension) {
		return dynamicMakeURL("/icons/" + guildId + '/' + hash, hash, size, extension);
	}

	public static String makeGuildSplashURL(String guildId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/splashes/" + guildId + '/' + hash, size, extension);
	}

	public static String makeGuildDiscoverySplashURL(String guildId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/discovery-splashes/" + guildId + '/' + hash, size, extension);
	}

	public static String makeGuildOrUserBannerURL(String guildOrUserId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/banners/" + guildOrUserId + '/' + hash, size, extension);
	}

	public static String makeDefaultUserAvatarURL(short userDiscriminator) {
		return makeURL("/embed/avatars/" + (userDiscriminator % 5), null, AllowedExtension.PNG);
	}

	public static String makeUserAvatarURL(String userId, String hash, AllowedSize size, AllowedExtension extension) {
		return dynamicMakeURL("/avatars/" + userId + '/' + hash, hash, size, extension);
	}

	public static String makeGuildMemberAvatarURL(String guildId, String userId, String hash, AllowedSize size, AllowedExtension extension) {
		return dynamicMakeURL("/guilds/" + guildId + "/users/" + userId + '/' + hash, hash, size, extension);
	}

	public static String makeApplicationIconURL(String applicationId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/app-icons/" + applicationId + '/' + hash, size, extension);
	}

	public static String makeRoleIconURL(String roleId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/role-icons/" + roleId + '/' + hash, size, extension);
	}

	public static String makeChannelIconURL(String groupDmId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/channel-icons/" + groupDmId + '/' + hash, size, extension);
	}

	public static interface URLFactory {
		String getHash();
		String makeURL(AllowedSize size, AllowedExtension extension);

		default String makeURL(AllowedSize size) {
			return makeURL(size, null);
		}

		default String makeURL(AllowedExtension extension) {
			return makeURL(null, extension);
		}

		default String makeURL() {
			return makeURL(null, null);
		}
	}
}
