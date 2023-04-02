package discord.enums;

public enum ApplicationCommandOptionType {
	Subcommand(1),
	SubcommandGroup(2),
	String(3),
	Integer(4),
	Boolean(5),
	User(6),
	Channel(7),
	Role(8),
	Mentionable(9),
	Number(10),
	Attachment(11);

	public static ApplicationCommandOptionType resolve(long value) {
		for (final var x : ApplicationCommandOptionType.values()) if (x.value == value) return x;
		return null;
	}

	public final int value;

	private ApplicationCommandOptionType(int value) {
		this.value = value;
	}
}
