package discord.resources;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.client.UserDiscordClient;
import discord.enums.GatewayEvent;
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

		private Flag(final int value) {
			this.bitIndex = value;
		}

		@Override
		public int getBitIndex() {
			return bitIndex;
		}
	}

	public User(final DiscordClient client, final SjObject data) {
		super(client, data);
	}

	@Override
	public String getApiPath() {
		return "/users/" + getId();
	}

	/**
	 * Sends a friend request to this user.
	 * @apiNote This API endpoint seems to be heavily monitored by Discord;
	 * you may get captchas very quickly. Use it sparingly.
	 * @throws ClassCastException if {@link #client} is not a {@link UserDiscordClient}.
	 */
	public CompletableFuture<Void> addFriend() {
		return ((UserDiscordClient) client).relationships.addFriendWithId(getId());
	}

	/**
	 * Blocks this user.
	 * @throws ClassCastException if {@link #client} is not a {@link UserDiscordClient}.
	 */
	public CompletableFuture<Void> block() {
		return ((UserDiscordClient) client).relationships.blockUser(getId());
	}

	/**
	 * Depending on the relationship type, either removes this user as a friend or unblocks them.
	 * @throws ClassCastException if {@link #client} is not a {@link UserDiscordClient}.
	 */
	public CompletableFuture<Void> deleteRelationship() {
		return ((UserDiscordClient) client).relationships.delete(getId());
	}

	public CompletableFuture<Void> setNote(final String note) {
		return client.api.put("/users/@me/notes/" + getId(), "{\"note\":\"" + note + "\"}").thenRun(Util.NO_OP);
	}

	public CompletableFuture<DMChannel> createDM() {
		return client.api.post("/users/@me/channels", "{\"recipient_id\":\"" + getId() + "\"}")
			.thenApply(r -> (DMChannel) client.channels.cache(r.asObject()));
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

	/**
	 * Overlays this user's data using the partial user object received from a 
	 * {@link GatewayEvent#PRESENCE_UPDATE} event.
	 * @param receivedUser A user object received from a {@link GatewayEvent#PRESENCE_UPDATE} event.
	 */
	public void overlay(final SjObject receivedUser) {
		if (receivedUser.containsKey("username"))
			data.put("username", receivedUser.get("username"));
		if (receivedUser.containsKey("avatar"))
			data.put("avatar", receivedUser.get("avatar"));
		if (receivedUser.containsKey("banner"))
			data.put("banner", receivedUser.get("banner"));
		if (receivedUser.containsKey("public_flags"))
			data.put("public_flags", receivedUser.get("public_flags"));
	}
}
