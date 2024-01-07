package discord.util;

/**
 * https://discord.com/developers/docs/reference#image-formatting
 */
public final class CDN {
	private static final String BASE_URL = "https://cdn.discordapp.com";

	public static enum AllowedSize {
		_16, _32, _64, _128, _256, _512, _1024, _2048, _4096;

		public final int value;

		private AllowedSize() {
			value = Integer.parseInt(name().substring(1));
			// Alternative implementation:
			// value = 1 << (4 + ordinal());
		}
	}

	public static enum AllowedExtension {
		WEBP, PNG, JPG, JPEG, GIF;

		public final String value;

		private AllowedExtension() {
			this.value = name().toLowerCase();
		}
	}

	private static String makeURL(String path, AllowedSize size, AllowedExtension extension) {
		// PNG is the default extension since every image type in Discord's CDN supports PNG
		return BASE_URL + path + '.' + ((extension == null) ? "png" : extension.value) + ((size == null) ? "" : ("?size=" + size.value));
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

	public static String makeBannerURL(String guildOrUserId, String hash, AllowedSize size, AllowedExtension extension) {
		return dynamicMakeURL("/banners/" + guildOrUserId + '/' + hash, hash, size, extension);
	}

	public static String makeDefaultUserAvatarURL(short userDiscriminator) {
		return makeURL("/embed/avatars/" + (userDiscriminator % 5), null, AllowedExtension.PNG);
	}

	public static String makeUserAvatarURL(String userId, String hash, AllowedSize size, AllowedExtension extension) {
		return dynamicMakeURL("/avatars/" + userId + '/' + hash, hash, size, extension);
	}

	public static String makeGuildMemberAvatarURL(String guildId, String userId, String hash, AllowedSize size, AllowedExtension extension) {
		return dynamicMakeURL("/guilds/" + guildId + "/users/" + userId + "/avatars/" + hash, hash, size, extension);
	}

	public static String makeUserAvatarDecorationURL(String userId, String hash, AllowedSize size, AllowedExtension extension) {
		return dynamicMakeURL("/avatar-decorations/" + userId + '/' + hash, hash, size, extension);
	}

	public static String makeApplicationIconOrCoverURL(String applicationId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/app-icons/" + applicationId + '/' + hash, size, extension);
	}

	public static String makeApplicationAssetURL(String applicationId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/app-assets/" + applicationId + '/' + hash, size, extension);
	}

	public static String makeRoleIconURL(String roleId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/role-icons/" + roleId + '/' + hash, size, extension);
	}

	public static String makeChannelIconURL(String groupDmId, String hash, AllowedSize size, AllowedExtension extension) {
		return makeURL("/channel-icons/" + groupDmId + '/' + hash, size, extension);
	}

	public static interface Image {
		String getHash();
		String getURL(AllowedSize size, AllowedExtension extension);

		default String getURL(AllowedSize size) {
			return getURL(size, null);
		}

		default String getURL(AllowedExtension extension) {
			return getURL(null, extension);
		}

		default String getURL() {
			return getURL(null, null);
		}
	}
}
