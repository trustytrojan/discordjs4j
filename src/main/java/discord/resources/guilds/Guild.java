package discord.resources.guilds;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.client.DiscordClient;
import discord.managers.ApplicationCommandManager;
import discord.managers.guild.GuildChannelManager;
import discord.managers.guild.GuildMemberManager;
import discord.managers.guild.RoleManager;
import discord.resources.Role;
import discord.resources.User;
import discord.resources.channels.Channel;
import discord.resources.channels.TextChannel;
import discord.resources.channels.VoiceChannel;
import discord.util.BitFlagSet;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.URLFactory;
import sj.SjObject;
import sj.SjSerializable;

/**
 * https://discord.com/developers/docs/resources/guild#guild-object
 */
public class Guild extends PreviewGuild {
	/**
	 * https://discord.com/developers/docs/resources/guild#create-guild-json-params
	 */
	public static class CreatePayload implements SjSerializable {
		public static record PartialChannel(String name, Channel.Type type) implements SjSerializable {
			@Override
			public String toJsonString() {
				return """
					{
						"name": \"%s\",
						"type": \"%d\"
					}
						""".formatted(name, type.value);
			}
		}

		public final String name;
		// icon
		public VerificationLevel verificationLevel;
		public DefaultMessageNotificationLevel defaultMessageNotifications;
		public ExplicitContentFilterLevel explicitContentFilter;
		public List<Role.Payload> roles;
		public List<PartialChannel> channels;
		public String afkChannelId;
		public Integer afkTimeout;
		public String systemChannelId;
		public BitFlagSet<SystemChannelFlag> systemChannelFlags;

		public CreatePayload(String name) {
			this.name = name;
		}

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			obj.put("name", name);
			if (verificationLevel != null) obj.put("verification_level", verificationLevel.ordinal());
			if (defaultMessageNotifications != null) obj.put("default_message_notifications", defaultMessageNotifications.ordinal());
			if (explicitContentFilter != null) obj.put("explicit_content_filter", explicitContentFilter.ordinal());
			if (roles != null) obj.put("roles", roles);
			if (channels != null) obj.put("channels", channels);
			if (afkChannelId != null) obj.put("afk_channel_id", afkChannelId);
			if (afkTimeout != null) obj.put("afk_timeout", afkTimeout);
			if (systemChannelId != null) obj.put("system_channel_id", systemChannelId);
			if (systemChannelFlags != null) obj.put("system_channel_flags", systemChannelFlags.asLong());
			return obj.toJsonString();
		}
	}

	/**
	 * https://discord.com/developers/docs/resources/guild#modify-guild-json-params
	 */
	public static class EditPayload implements SjSerializable {
		public String name;
		public VerificationLevel verificationLevel;
		public DefaultMessageNotificationLevel defaultMessageNotifications;
		public ExplicitContentFilterLevel explicitContentFilter;
		public String afkChannelId;
		public Integer afkTimeout;
		// icon
		public String ownerId;
		// splash
		// discovery_splash
		// banner
		public String systemChannelId;
		public BitFlagSet<SystemChannelFlag> systemChannelFlags;
		public String rulesChannelId;
		public String publicUpdatesChannelId;
		public String preferredLocale;
		public List<Feature> features;
		public String description;
		public Boolean premiumProgressBarEnabled;
		public String safetyAlertsChannelId;

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			if (name != null) obj.put("name", name);
			if (verificationLevel != null) obj.put("verification_level", verificationLevel.ordinal());
			if (defaultMessageNotifications != null) obj.put("default_message_notifications", defaultMessageNotifications.ordinal());
			if (explicitContentFilter != null) obj.put("explicit_content_filter", explicitContentFilter.ordinal());
			if (afkChannelId != null) obj.put("afk_channel_id", afkChannelId);
			if (afkTimeout != null) obj.put("afk_timeout", afkTimeout);
			if (ownerId != null) obj.put("owner_id", ownerId);
			if (systemChannelId != null) obj.put("system_channel_id", systemChannelId);
			if (systemChannelFlags != null) obj.put("system_channel_flags", systemChannelFlags.asLong());
			if (rulesChannelId != null) obj.put("rules_channel_id", rulesChannelId);
			if (publicUpdatesChannelId != null) obj.put("public_updates_channel_id", publicUpdatesChannelId);
			if (preferredLocale != null) obj.put("preferred_locale", preferredLocale);
			if (features != null) obj.put("features", features);
			if (description != null) obj.put("description", description);
			if (premiumProgressBarEnabled != null) obj.put("premium_progress_bar_enabled", premiumProgressBarEnabled);
			if (safetyAlertsChannelId != null) obj.put("safety_alerts_channel_id", safetyAlertsChannelId);
			return obj.toJsonString();
		}
	}

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
	public static enum SystemChannelFlag implements BitFlagSet.BitFlag {
		SUPPRESS_JOIN_NOTIFICATIONS(1 << 0),
		SUPPRESS_PREMIUM_SUBSCRIPTIONS(1 << 1),
		SUPPRESS_GUILD_REMINDER_NOTIFICATIONS(1 << 2),
		SUPPRESS_JOIN_NOTIFICATION_REPLIES(1 << 3),
		SUPPRESS_ROLE_SUBSCRIPTION_PURCHASE_NOTIFICATIONS(1 << 4),
		SUPPRESS_ROLE_SUBSCRIPTION_PURCHASE_NOTIFICATION_REPLIES(1 << 5);
	
		private final int value;
	
		private SystemChannelFlag(int value) {
			this.value = value;
		}

		@Override
		public long value() {
			return value;
		}
	}

	/**
	 * https://discord.com/developers/docs/resources/guild#guild-object-guild-features
	 */
	public static enum Feature implements SjSerializable {
		ANIMATED_BANNER,
		ANIMATED_ICON,
		APPLICATION_COMMAND_PERMISSIONS_V2,
		AUTO_MODERATION,
		BANNER,
		COMMUNITY,
		CREATOR_MONETIZABLE_PROVISIONAL,
		CREATOR_STORE_PAGE,
		DEVELOPER_SUPPORT_SERVER,
		DISCOVERABLE,
		FEATURABLE,
		INVITES_DISABLED,
		INVITE_SPLASH,
		MEMBER_VERIFICATION_GATE_ENABLED,
		MORE_STICKERS,
		NEWS,
		PARTNERED,
		PREVIEW_ENABLED,
		RAID_ALERTS_DISABLED,
		ROLE_ICONS,
		ROLE_SUBSCRIPTIONS_AVAILABLE_FOR_PURCHASE,
		ROLE_SUBSCRIPTIONS_ENABLED,
		TICKETED_EVENTS_ENABLED,
		VANITY_URL,
		VERIFIED,
		VIP_REGIONS,
		WELCOME_SCREEN_ENABLED;

		@Override
		public String toJsonString() {
			return '"' + name() + '"';
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

	public String ownerId() {
		return data.getString("owner_id");
	}

	public CompletableFuture<User> getOwner() {
		return client.users.get(ownerId());
	}

	public String afkChannelId() {
		return data.getString("afk_channel_id");
	}

	public CompletableFuture<VoiceChannel> getAfkChannel() {
		return client.channels.get(afkChannelId()).thenApply(c -> (VoiceChannel) c);
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
		return client.channels.get(systemChannelId()).thenApply(c -> (TextChannel) c);
	}

	public BitFlagSet<SystemChannelFlag> systemChannelFlags() {
		return new BitFlagSet<>(data.getLong("system_channel_flags"));
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
		return client.channels.get(publicUpdatesChannelId()).thenApply(c -> (TextChannel) c);
	}

	public Integer maxVideoChannelUsers() {
		return data.getInteger("max_video_channel_users");
	}

	public Integer maxStageVideoChannelUsers() {
		return data.getInteger("max_stage_video_channel_users");
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
		return client.channels.get(safetyAlertsChannelId()).thenApply(c -> (TextChannel) c);
	}
}