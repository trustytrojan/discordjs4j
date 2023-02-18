package discord.enums;

public enum ChannelType {
	GuildText(0),
	DM(1),
	GuildVoice(2),
	GroupDM(3),
	GuildCategory(4),
	GuildAnnouncement(5),
	AnnouncementThread(10),
	PublicThread(11),
	PrivateThread(12),
	GuildStageVoice(13),
	GuildDirectory(14),
	GuildForum(15);

	public final int value;

	private ChannelType(int value) {
		this.value = value;
	}

	public static ChannelType get(long value) {
		for(final var x : ChannelType.values())
			if(x.value == value) return x;
		return null;
	}

	public String toString() {
		return switch(this) {
			case GuildText -> "Text Channel";
			case DM -> "DM Channel";
			case GuildVoice -> "Voice Channel";
			case GroupDM -> "Group DM";
			case GuildCategory -> "Category Channel";
			case GuildAnnouncement -> "Announcement Channel";
			case AnnouncementThread -> "Announcement Thread";
			case PublicThread -> "Public Thread";
			case PrivateThread -> "Private Thread";
			case GuildStageVoice -> "Stage Channel";
			//case GuildDirectory -> "";
			case GuildForum -> "Forum Channel";
			default -> null;
		};
	}
}
