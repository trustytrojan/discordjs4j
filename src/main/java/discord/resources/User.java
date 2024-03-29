package discord.resources;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.client.UserDiscordClient;
import discord.resources.channels.DMChannel;
import discord.util.BitFlagSet;
import discord.util.BitFlagSet.BitFlag;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.Image;
import discord.util.Util;
import sj.SjObject;

public class User extends AbstractDiscordResource {
	public static enum Flag implements BitFlag {
		STAFF,
		PARTNER,
		HYPESQUAD,
		BUG_HUNTER_LEVEL_1,
		HYPESQUAD_ONLINE_HOUSE_1(6),
		HYPESQUAD_ONLINE_HOUSE_2(7),
		HYPESQUAD_ONLINE_HOUSE_3(8),
		PREMIUM_EARLY_SUPPORTER(9),
		TEAM_PSEUDO_USER(10),
		BUG_HUNTER_LEVEL_2(14),
		VERIFIED_BOT(16),
		VERIFIED_DEVELOPER(17),
		CERTIFIED_MODERATOR(18),
		BOT_HTTP_INTERACTIONS(19),
		ACTIVE_DEVELOPER(22);

		private final int bitIndex;

		private Flag() {
			bitIndex = ordinal();
		}

		private Flag(int value) {
			this.bitIndex = value;
		}

		@Override
		public int getBitIndex() {
			return bitIndex;
		}
	}

	public User(DiscordClient client, SjObject data) {
		super(client, data);
	}

	@Override
	public String getApiPath() {
		return "/users/" + getId();
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
		return ((UserDiscordClient) client).relationships.addFriendWithId(getId());
	}

	/**
	 * <b>USER-ONLY METHOD:</b> If {@code this.client} is not an instance of
	 * {@code UserDiscordClient}, a {@code ClassCastException} will be thrown.
	 */
	public CompletableFuture<Void> block() {
		return ((UserDiscordClient) client).relationships.blockUser(getId());
	}

	/**
	 * <b>USER-ONLY METHOD:</b> If {@code this.client} is not an instance of
	 * {@code UserDiscordClient}, a {@code ClassCastException} will be thrown.
	 * <p>
	 * Either removes this user as a friend or unblocks them.
	 */
	public CompletableFuture<Void> deleteRelationship() {
		return ((UserDiscordClient) client).relationships.delete(getId());
	}

	public CompletableFuture<Void> setNote(String note) {
		return client.api.put("/users/@me/notes/" + getId(), "{\"note\":\"" + note + "\"}").thenRun(Util.NO_OP);
	}

	public CompletableFuture<DMChannel> createDM() {
		return client.api.post("/users/@me/channels", "{\"recipient_id\":\"" + getId() + "\"}")
				.thenApply(r -> new DMChannel(client, r.asObject()));
	}

	public boolean isBot() {
		return data.getBooleanDefaultFalse("bot");
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

	public final Image avatar = new Image() {
		@Override
		public String getHash() {
			return data.getString("avatar");
		}

		@Override
		public String getURL(AllowedSize size, AllowedExtension extension) {
			final var hash = getHash();
			return (hash == null)
					? CDN.makeDefaultUserAvatarURL(getDiscriminator())
					: CDN.makeUserAvatarURL(getId(), hash, size, extension);
		}
	};

	public final Image banner = new Image() {
		@Override
		public String getHash() {
			return data.getString("banner");
		}

		@Override
		public String getURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeBannerURL(getId(), getHash(), size, extension);
		}
	};

	public BitFlagSet<Flag> getPublicFlags() {
		return new BitFlagSet<>(data.getInteger("public_flags"));
	}
}
