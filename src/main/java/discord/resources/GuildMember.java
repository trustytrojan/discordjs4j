package discord.resources;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.managers.guild.GuildMemberRoleManager;
import discord.resources.guilds.Guild;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.URLFactory;
import sj.SjObject;

public class GuildMember extends AbstractDiscordResource implements GuildResource {
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

	public GuildMember(DiscordClient client, Guild guild, SjObject data) {
		super(client, guild, data, data.getObject("user").getString("id"));
		roles = new GuildMemberRoleManager(client, this);
	}

	@Override
	public String getId() {
		return getData().getObject("user").getString(getId());
	}

	@Override
	public String getGuildId() {
		return guild.id;
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

	public final URLFactory avatar = new URLFactory() {
		@Override
		public String getHash() {
			return data.getString("avatar");
		}

		@Override
		public String makeURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeGuildMemberAvatarURL(getGuildId(), id, getHash(), size, extension);
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
