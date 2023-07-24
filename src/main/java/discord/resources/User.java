package discord.resources;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.client.UserDiscordClient;
import discord.resources.channels.DMChannel;
import discord.util.BitFlagSet;
import discord.util.BitFlagSet.BitFlagEnum;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.URLFactory;
import discord.util.Util;
import sj.SjObject;

public class User extends AbstractDiscordResource {
	public static enum Flag implements BitFlagEnum {
		STAFF(1 << 0),
		PARTNER(1 << 1),
		HYPESQUAD(1 << 2),
		BUG_HUNTER_LEVEL_1(1 << 3),
		HYPESQUAD_ONLINE_HOUSE_1(1 << 6),
		HYPESQUAD_ONLINE_HOUSE_2(1 << 7),
		HYPESQUAD_ONLINE_HOUSE_3(1 << 8),
		PREMIUM_EARLY_SUPPORTER(1 << 9),
		TEAM_PSEUDO_USER(1 << 10),
		BUG_HUNTER_LEVEL_2(1 << 14),
		VERIFIED_BOT(1 << 16),
		VERIFIED_DEVELOPER(1 << 17),
		CERTIFIED_MODERATOR(1 << 18),
		BOT_HTTP_INTERACTIONS(1 << 19),
		ACTIVE_DEVELOPER(1 << 22);

		private final int value;

		private Flag(int value) {
			this.value = value;
		}

		@Override
		public long getBit() {
			return value;
		}
	}

	public User(DiscordClient client, SjObject data) {
		super(client, data, "/users");
	}

	/**
	 * <b>USER-ONLY METHOD:</b> If {@code this.client} is not an instance of
	 * {@code UserDiscordClient}, a {@code ClassCastException} will be thrown.
	 * <p>
	 * <b>WARNING:</b> This API method is heavily monitored by Discord. It is very likely
	 * that this will throw a {@code DiscordAPIException} with the response body
	 * containing captcha-related data. It is advised not to use this endpoint as a
	 * user.
	 */
	public CompletableFuture<Void> addFriend() {
		return ((UserDiscordClient) client).relationships.addFriendWithId(id);
	}

	/**
	 * <b>USER-ONLY METHOD:</b> If {@code this.client} is not an instance of
	 * {@code UserDiscordClient}, a {@code ClassCastException} will be thrown.
	 */
	public CompletableFuture<Void> block() {
		return ((UserDiscordClient) client).relationships.blockUser(id);
	}

	/**
	 * <b>USER-ONLY METHOD:</b> If {@code this.client} is not an instance of
	 * {@code UserDiscordClient}, a {@code ClassCastException} will be thrown.
	 * <p>
	 * Either removes this user as a friend or unblocks them.
	 */
	public CompletableFuture<Void> deleteRelationship() {
		return ((UserDiscordClient) client).relationships.delete(id);
	}

	public CompletableFuture<Void> setNote(String note) {
		return client.api.put("/users/@me/notes/" + id, "{\"note\":\"" + note + "\"}").thenRun(Util.NO_OP);
	}

	public CompletableFuture<DMChannel> createDM() {
		return client.api.post("/users/@me/channels", "{\"recipient_id\":\"" + id + "\"}")
				.thenApply(r -> new DMChannel(client, r.toJsonObject()));
	}

	public String getUsername() {
		return data.getString("username");
	}

	public short getDiscriminator() {
		return Short.parseShort(data.getString("discriminator"));
	}

	public String getTag() {
		return getUsername() + '#' + getDiscriminator();
	}

	public boolean isBot() {
		return data.getBooleanDefaultFalse("bot");
	}

	public final URLFactory avatar = new URLFactory() {
		@Override
		public String getHash() {
			return data.getString("avatar");
		}

		@Override
		public String makeURL(AllowedSize size, AllowedExtension extension) {
			final var hash = getHash();
			return (hash == null)
					? CDN.makeDefaultUserAvatarURL(getDiscriminator())
					: CDN.makeUserAvatarURL(id, hash, size, extension);
		}
	};

	public final URLFactory banner = new URLFactory() {
		@Override
		public String getHash() {
			return data.getString("banner");
		}

		@Override
		public String makeURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeGuildOrUserBannerURL(id, getHash(), size, extension);
		}
	};

	public BitFlagSet<Flag> getPublicFlags() {
		return new BitFlagSet<>(data.getInteger("public_flags"));
	}
}
