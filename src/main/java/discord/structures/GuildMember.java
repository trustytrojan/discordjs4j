package discord.structures;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import discord.client.DiscordClient;
import discord.managers.guild.GuildMemberRoleManager;
import discord.util.CDN;
import discord.util.CDN.URLFactory;
import simple_json.SjObject;

public class GuildMember implements GuildResource {
	public static enum Flags {
		DID_REJOIN(1 << 0),
		COMPLETED_ONBOARDING(1 << 1),
		BYPASSES_VERIFICATION(1 << 2),
		STARTED_ONBOARDING(1 << 3);

		public final int value;

		private Flags(int value) {
			this.value = value;
		}
	}

	private final DiscordClient client;
	private SjObject data;

	public final GuildMemberRoleManager roles;

	public final User user;
	public final Guild guild;

	public GuildMember(final DiscordClient client, final Guild guild, final SjObject data) {
		this.client = client;
		this.guild = guild;
		setData(data);
		user = client.users.fetch(data.getObject("user").getString("id")).join();
		roles = new GuildMemberRoleManager(client, this);
	}

	public String nickname() {
		return data.getString("nick");
	}

	public final URLFactory avatar = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("avatar");
		}

		@Override
		public String url(final int size, final String extension) {
			return CDN.guildMemberAvatar(guild.id(), user.id(), hash(), size, extension);
		}
	};

	public Instant joinedAt() {
		return Instant.parse(data.getString("joined_at"));
	}

	public Instant premiumSince() {
		return Instant.parse(data.getString("premium_since"));
	}

	public boolean deaf() {
		return data.getBoolean("deaf");
	}

	public boolean mute() {
		return data.getBoolean("mute");
	}

	public Set<Flags> flags() {
		final var bitset = data.getLong("flags").shortValue();
		final var flags = new HashSet<Flags>();
		for (final var flag : Flags.values())
			if (bitset / flag.value == 1)
				flags.add(flag);
		return flags;
	}

	public boolean pending() {
		return data.getBoolean("pending");
	}

	// permissions

	public Instant communicationDisabledUntil() {
		return Instant.parse(data.getString("communication_disabled_until"));
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public SjObject getData() {
		return data;
	}

	@Override
	public void setData(final SjObject data) {
		this.data = data;
	}

	@Override
	public String id() {
		return user.id();
	}

	@Override
	public String apiPath() {
		return "/guilds/" + guild.id() + "/members/" + user.id();
	}

	@Override
	public Guild guild() {
		return guild;
	}
}
