package discord.enums;

public enum CommandType {
	ChatInput(1),
	Message(2),
	User(3);

	public static CommandType get(long value) {
		for(final var x : CommandType.values())
			if(x.value == value) return x;
		return null;
	}

	public final int value;

	private CommandType(int value) {
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
