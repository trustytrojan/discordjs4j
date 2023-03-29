package discord.enums;

public enum InteractionType {
	Ping(1),
	ApplicationCommand(2),
	MessageComponent(3),
	ApplicationCommandAutocomplete(4),
	ModalSubmit(5);

	public static InteractionType resolve(int value) {
		for (final var x : InteractionType.values())
			if (x.value == value)
				return x;
		return null;
	}

	public final int value;

	private InteractionType(int value) {
		this.value = value;
	}
}
