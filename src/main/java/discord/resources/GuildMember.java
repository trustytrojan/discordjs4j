package discord.resources;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.managers.GuildMemberRoleManager;
import discord.resources.guilds.Guild;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.Image;
import sj.SjObject;

public class GuildMember extends AbstractGuildResource {
	public static enum Flags {
		DID_REJOIN,
		COMPLETED_ONBOARDING,
		BYPASSES_VERIFICATION,
		STARTED_ONBOARDING;

		public final int value;

		private Flags() {
			this.value = (1 << ordinal());
		}
	}

	public final GuildMemberRoleManager roles;

	public GuildMember(DiscordClient client, SjObject data, Guild guild) {
		super(client, data, guild);
		roles = new GuildMemberRoleManager(client, this);
	}

	@Override
	public String getApiPath() {
		throw new UnsupportedOperationException("Guild members cannot be fetched individually");
	}

	@Override
	public String getId() {
		return getData().getObject("user").getString("id");
	}

	public CompletableFuture<User> getUserAsync() {
		return client.users.get(getId());
	}

	public User getUser() {
		return getUserAsync().join();
	}

	public String getNickname() {
		return data.getString("nick");
	}

	public final Image avatar = new Image() {
		@Override
		public String getHash() {
			return data.getString("avatar");
		}

		@Override
		public String getURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeGuildMemberAvatarURL(getGuildId(), getId(), getHash(), size, extension);
		}
	};

	public Instant joinedAt() {
		return Instant.parse(data.getString("joined_at"));
	}

	public Instant premiumSince() {
		return Instant.parse(data.getString("premium_since"));
	}

	public boolean isDeaf() {
		return data.getBoolean("deaf");
	}

	public boolean isMute() {
		return data.getBoolean("mute");
	}

	public boolean isPending() {
		return data.getBoolean("pending");
	}

	// permissions

	public Instant communicationDisabledUntil() {
		return Instant.parse(data.getString("communication_disabled_until"));
	}
}
