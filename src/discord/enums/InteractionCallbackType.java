package discord.enums;

public enum InteractionCallbackType {
	Pong(1),
	ChannelMessageWithSource(4),
	DeferredChannelMessageWithSource(5),
	DeferredUpdateMessage(6),
	UpdateMessage(7),
	ApplicationCommandAutocompleteResult(8),
	Modal(9);

	public final int value;

	private InteractionCallbackType(int value) {
		this.value = value;
	}
}
