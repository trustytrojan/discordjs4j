package discord.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.channels.DMChannel;
import discord.util.BitFlagSet.BitFlag;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.URLFactory;
import discord.util.Util;
import sj.SjObject;

public class User extends AbstractDiscordResource implements Mentionable {
	public static enum Flag implements BitFlag {
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
		public long value() {
			return value;
		}
	}

	private final String mention = "<@" + id + '>';

	public User(DiscordClient client, SjObject data) {
		super(client, data, "/users");
	}

	public CompletableFuture<Void> setNote(String note) {
		final var body = """
				{
					"note": \"%s\"
				}
				""".formatted(note);
		return client.api.put("/users/@me/notes/" + id, body).thenRun(Util.NO_OP);
	}

	public CompletableFuture<DMChannel> createDM() {
		return client.api.post("/users/@me/channels", """
				{
					"recipient_id": "%s"
				}
				""".formatted(id))
			.thenApply(r -> new DMChannel(client, r.toJsonObject()));
	}

	@Override
	public String mention() {
		return mention;
	}

	public String username() {
		return data.getString("username");
	}

	public short discriminator() {
		return Short.parseShort(data.getString("discriminator"));
	}

	public String tag() {
		return username() + '#' + discriminator();
	}

	public boolean bot() {
		return data.getBooleanDefaultFalse("bot");
	}

	public final URLFactory avatar = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("avatar");
		}

		@Override
		public String url(AllowedSize size, AllowedExtension extension) {
			final var hash = hash();
			return (hash == null)
				? CDN.defaultUserAvatar(discriminator())
				: CDN.userAvatar(id, hash, size, extension);
		}
	};

	public final URLFactory banner = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("banner");
		}

		@Override
		public String url(AllowedSize size, AllowedExtension extension) {
			return CDN.guildOrUserBanner(id, hash(), size, extension);
		}
	};

	public List<Flag> publicFlags() {
		return computeFlags(data.getLong("public_flags").intValue());
	}

	protected static List<Flag> computeFlags(int bitset) {
		final var flags = new ArrayList<Flag>();
		for (final var flag : Flag.values()) {
			if ((bitset & flag.value) != 0) {
				flags.add(flag);
			}
		}
		return Collections.unmodifiableList(flags);
	}
}
