package discord.enums;

public enum AuditLogEvent {
	GuildUpdate(1),
	ChannelCreate(10),
	ChannelUpdate(11),
	ChannelDelete(12),
	ChannelOverwriteCreate(13),
	ChannelOverwriteUpdate(14),
	ChannelOverwriteDelete(15),
	MemberKick(20),
	MemberPrune(21),
	MemberBanAdd(22),
	MemberBanRemove(23),
	MemberUpdate(24),
	MemberRoleUpdate(25),
	MemberMove(26),
	MemberDisconnect(27),
	BotAdd(28),
	RoleCreate(30),
	RoleUpdate(31),
	RoleDelete(32),
	InviteCreate(40),
	InviteUpdate(41),
	InviteDelete(42),
	WebhookCreate(50),
	WebhookUpdate(51),
	WebhookDelete(52),
	EmojiCreate(60),
	EmojiUpdate(61),
	EmojiDelete(62),
	MessageDelete(72),
	MessageBulkDelete(73),
	MessagePin(74),
	MessageUnpin(75),
	IntegrationCreate(80),
	IntegrationUpdate(81),
	IntegrationDelete(82),
	StageInstanceCreate(83),
	StageInstanceUpdate(84),
	StageInstanceDelete(85),
	StickerCreate(90),
	StickerUpdate(91),
	StickerDelete(92),
	GuildScheduledEventCreate(100),
	GuildScheduledEventUpdate(101),
	GuildScheduledEventDelete(102),
	ThreadCreate(110),
	ThreadUpdate(111),
	ThreadDelete(112),
	ApplicationCommandPermissionUpdate(121),
	AutoModerationRuleCreate(140),
	AutoModerationRuleUpdate(141),
	AutoModerationRuleDelete(142),
	AutoModerationBlockMessage(143),
	AutoModerationFlagToChannel(144),
	AutoModerationUserCommunicationDisabled(145);

	public static AuditLogEvent get(long value) {
		for (final var x : AuditLogEvent.values()) if (x.value == value) return x;
		return null;
	}

	public final int value;

	private AuditLogEvent(int value) {
		this.value = value;
	}
}
