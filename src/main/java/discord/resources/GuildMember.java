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

public class GuildMember extends AbstractGuildResource {
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

	public final GuildMemberRoleManager roles;

	public GuildMember(DiscordClient client, Guild guild, SjObject data) {
		super(client, guild, data, data.getObject("user").getString("id"));
		roles = new GuildMemberRoleManager(client, this);
	}

	public CompletableFuture<User> getUserAsync() {
		return client.users.get(id);
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
			return CDN.makeGuildMemberAvatarURL(guild.id, id, getHash(), size, extension);
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
