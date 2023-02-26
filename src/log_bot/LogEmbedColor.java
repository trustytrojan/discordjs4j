package log_bot;

import discord.enums.AuditLogEvent;

public final class LogEmbedColor {
	
	private static final String _created = "43b581";
	private static final String _updated = "faa61a";
	private static final String _deleted = "f04747";

	static String get(AuditLogEvent event) {
		switch (event) {
			case ChannelCreate:
			case ChannelOverwriteCreate:
			case RoleCreate:
			case InviteCreate:
			case WebhookCreate:
			case EmojiCreate:
			case IntegrationCreate:
			case StageInstanceCreate:
			case StickerCreate:
			case GuildScheduledEventCreate:
			case ThreadCreate:
			case AutoModerationRuleCreate:
				return _created;
			case ChannelDelete:
			case ChannelOverwriteDelete:
			case RoleDelete:
			case InviteDelete:
			case WebhookDelete:
			case EmojiDelete:
			case IntegrationDelete:
			case StageInstanceDelete:
			case StickerDelete:
			case GuildScheduledEventDelete:
			case ThreadDelete:
			case AutoModerationRuleDelete:
				return _deleted;
			case ChannelUpdate:
			case ChannelOverwriteUpdate:
			case RoleUpdate:
			case InviteUpdate:
			case WebhookUpdate:
			case EmojiUpdate:
			case IntegrationUpdate:
			case StageInstanceUpdate:
			case StickerUpdate:
			case GuildScheduledEventUpdate:
			case ThreadUpdate:
			case AutoModerationRuleUpdate:
				return _updated;
			default:
				return null;
		}
	}

}
