package discord.structures;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import discord.client.DiscordClient;
import discord.util.CDN;
import discord.util.CDN.URLFactory;
import simple_json.JSONObject;

public class GuildMember implements GuildResource {
	private final DiscordClient client;
	private JSONObject data;

	public final User user;
	public final Guild guild;

	public GuildMember(DiscordClient client, Guild guild, JSONObject data) {
		this.client = client;
		this.guild = guild;
		this.data = data;
		user = client.users.fetch(data.getObject("user").getString("id")).join();
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
		public String url(int size, String extension) {
			return CDN.guildMemberAvatar(guildId(), user.id(), hash(), size, extension);
		}
	};

	// roles

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
	public JSONObject getData() {
		return data;
	}

	@Override
	public void setData(JSONObject data) {
		this.data = data;
	}

	@Override
	public Guild guild() {
		return guild;
	}

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
}
