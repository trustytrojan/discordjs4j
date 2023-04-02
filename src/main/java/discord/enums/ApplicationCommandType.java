package discord.enums;

public enum ApplicationCommandType {
	ChatInput(1),
	Message(2),
	User(3);

	public static ApplicationCommandType get(long value) {
		for (final var x : ApplicationCommandType.values()) if (x.value == value) return x;
		return null;
	}

	public final int value;

	private ApplicationCommandType(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return switch(this) {
			case ChatInput -> "Chat Input";
			case Message -> "Message";
			case User -> "User";
			default -> null;
		};
	}
}
