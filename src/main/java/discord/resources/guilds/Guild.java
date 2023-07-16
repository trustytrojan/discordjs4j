package discord.resources.guilds;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.client.DiscordClient;
import discord.managers.ApplicationCommandManager;
import discord.managers.guild.GuildChannelManager;
import discord.managers.guild.GuildMemberManager;
import discord.managers.guild.RoleManager;
import discord.resources.AbstractDiscordResource;
import discord.resources.User;
import discord.resources.channels.TextChannel;
import discord.resources.channels.VoiceChannel;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.URLFactory;
import sj.SjObject;

/**
 * https://discord.com/developers/docs/resources/guild#guild-object
 */
public class Guild extends AbstractDiscordResource {
	/**
	 * https://discord.com/developers/docs/resources/guild#guild-object-default-message-notification-level
	 */ 
	public static enum DefaultMessageNotificationLevel { ALL_MESSAGES, ONLY_MENTIONS }

	/**
	 * https://discord.com/developers/docs/resources/guild#guild-object-explicit-content-filter-level
	 */
	public static enum ExplicitContentFilterLevel { DISABLED, MEMBERS_WITHOUT_ROLES, ALL_MEMBERS }

	/**
	 * https://discord.com/developers/docs/resources/guild#guild-object-mfa-level
	 */
	public static enum MFALevel { NONE, ELEVATED }

	/**
	 * https://discord.com/developers/docs/resources/guild#guild-object-verification-level
	 */
	public static enum VerificationLevel { NONE, LOW, MEDIUM, HIGH, VERY_HIGH }

	/**
	 * https://discord.com/developers/docs/resources/guild#guild-object-guild-nsfw-level
	 */
	public static enum NSFWLevel { DEFAULT, EXPLICIT, SAFE, AGE_RESTRICTED }

	/**
	 * https://discord.com/developers/docs/resources/guild#guild-object-premium-tier
	 */
	public static enum PremiumTier { NONE, TIER_1, TIER_2, TIER_3 }

	/**
	 * https://discord.com/developers/docs/resources/guild#guild-object-system-channel-flags
	 */
	public static class SystemChannelFlags {
		private final long bitset;

		private SystemChannelFlags(long bitset) {
			this.bitset = bitset;
		}

		public boolean has(Flag flag) {
			return (bitset & flag.value) != 0;
		}

		public static enum Flag {
			SUPPRESS_JOIN_NOTIFICATIONS(1 << 0),
			SUPPRESS_PREMIUM_SUBSCRIPTIONS(1 << 1),
			SUPPRESS_GUILD_REMINDER_NOTIFICATIONS(1 << 2),
			SUPPRESS_JOIN_NOTIFICATION_REPLIES(1 << 3),
			SUPPRESS_ROLE_SUBSCRIPTION_PURCHASE_NOTIFICATIONS(1 << 4),
			SUPPRESS_ROLE_SUBSCRIPTION_PURCHASE_NOTIFICATION_REPLIES(1 << 5);
		
			public final int value;
		
			private Flag(int value) {
				this.value = value;
			}
		}
	}

	/**
	 * https://discord.com/developers/docs/resources/guild#welcome-screen-object
	 */
	public static class WelcomeScreen {
		/**
		 * https://discord.com/developers/docs/resources/guild#welcome-screen-object-welcome-screen-channel-structure
		 */
		public static class WelcomeScreenChannel {
			public final String channelId,
								description,
								emojiId,
								emojiName;

			private WelcomeScreenChannel(SjObject data) {
				channelId = data.getString("channel_id");
				description = data.getString("description");
				emojiId = data.getString("emoji_id");
				emojiName = data.getString("emoji_name");
			}
		}

		public final String description;
		public final List<WelcomeScreenChannel> channels;

		private WelcomeScreen(SjObject data) {
			description = data.getString("description");
			channels = data.getObjectArray("welcome_channels").stream().map(WelcomeScreenChannel::new).toList();
		}
	}

	public final GuildMemberManager members;
	public final GuildChannelManager channels;
	public final RoleManager roles;
	public final ApplicationCommandManager commands;

	public Guild(DiscordClient client, SjObject data) {
		super(client, data);
		channels = new GuildChannelManager(client, this);
		roles = new RoleManager(client, this);
		members = new GuildMemberManager(client, this);
		commands = (client instanceof final BotDiscordClient bot && bot.application != null)
				? new ApplicationCommandManager(bot, id)
				: null;
	}

	public String name() {
		return data.getString("name");
	}

	public final URLFactory icon = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("icon");
		}

		@Override
		public String url(AllowedSize size, AllowedExtension extension) {
			return CDN.guildIcon(id, hash(), size, extension);
		}
	};

	public final URLFactory splash = new CDN.URLFactory() {
		@Override
		public String hash() {
			return data.getString("splash");
		}

		@Override
		public String url(AllowedSize size, AllowedExtension extension) {
			return CDN.guildSplash(id, hash(), size, extension);
		}
	};

	public final URLFactory discoverySplash = new CDN.URLFactory() {
		@Override
		public String hash() {
			return data.getString("discovery_splash");
		}

		@Override
		public String url(AllowedSize size, AllowedExtension extension) {
			return CDN.guildDiscoverySplash(id, hash(), size, extension);
		}
	};

	public String ownerId() {
		return data.getString("owner_id");
	}

	public CompletableFuture<User> getOwner() {
		return client.users.fetch(ownerId());
	}

	public String afkChannelId() {
		return data.getString("afk_channel_id");
	}

	public CompletableFuture<VoiceChannel> getAfkChannel() {
		return client.channels.fetch(afkChannelId()).thenApply(c -> (VoiceChannel) c);
	}

	public VerificationLevel verificationLevel() {
		return VerificationLevel.values()[data.getInteger("verification_level")];
	}

	public DefaultMessageNotificationLevel defaultMessageNotifications() {
		return DefaultMessageNotificationLevel.values()[data.getInteger("default_message_notifications")];
	}

	public ExplicitContentFilterLevel explicitContentFilter() {
		return ExplicitContentFilterLevel.values()[data.getInteger("explicit_content_filter")];
	}

	/**
	 * https://discord.com/developers/docs/resources/guild#guild-object-guild-features
	 */
	public List<String> features() {
		return data.getArray("features").stream().map(o -> (String) o).toList();
	}

	public MFALevel mfaLevel() {
		return MFALevel.values()[data.getInteger("mfa_level")];
	}

	public String applicationId() {
		return data.getString("application_id");
	}

	public String systemChannelId() {
		return data.getString("system_channel_id");
	}

	public CompletableFuture<TextChannel> getSystemChannel() {
		return client.channels.fetch(systemChannelId()).thenApply(c -> (TextChannel) c);
	}

	public SystemChannelFlags systemChannelFlags() {
		return new SystemChannelFlags(data.getLong("system_channel_flags"));
	}

	public String rulesChannelId() {
		return data.getString("rules_channel_id");
	}

	public Integer maxPresences() {
		return data.getInteger("max_presences");
	}

	public Integer maxMembers() {
		return data.getInteger("max_members");
	}

	public String vanityUrlCode() {
		return data.getString("vanity_url_code");
	}

	public String description() {
		return data.getString("description");
	}

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

	public PremiumTier premiumTier() {
		return PremiumTier.values()[data.getInteger("premium_tier")];
	}

	public Integer premiumSubscriptionCount() {
		return data.getInteger("premium_subscription_count");
	}

	public String preferredLocale() {
		return data.getString("preferred_locale");
	}

	public String publicUpdatesChannelId() {
		return data.getString("public_updates_channel_id");
	}

	public CompletableFuture<TextChannel> getPublicUpdatesChannel() {
		return client.channels.fetch(publicUpdatesChannelId()).thenApply(c -> (TextChannel) c);
	}

	public Integer maxVideoChannelUsers() {
		return data.getInteger("max_video_channel_users");
	}

	public Integer maxStageVideoChannelUsers() {
		return data.getInteger("max_stage_video_channel_users");
	}

	public Integer approximateMemberCount() {
		return data.getInteger("approximate_member_count");
	}

	public Integer approximatePresenceCount() {
		return data.getInteger("approximate_presence_count");
	}

	public WelcomeScreen welcomeScreen() {
		final var o = data.getObject("id");
		return (o == null) ? null : new WelcomeScreen(o);
	}

	public NSFWLevel nsfwLevel() {
		return NSFWLevel.values()[data.getInteger("nsfw_level")];
	}

	public boolean premiumProgressBarEnabled() {
		return data.getBoolean("premium_progress_bar_enabled");
	}

	public String safetyAlertsChannelId() {
		return data.getString("safety_alerts_channel_id");
	}

	public CompletableFuture<TextChannel> getSafetyAlertsChannel() {
		return client.channels.fetch(safetyAlertsChannelId()).thenApply(c -> (TextChannel) c);
	}

	@Override
	public String apiPath() {
		return "/guilds/" + id;
	}
}
