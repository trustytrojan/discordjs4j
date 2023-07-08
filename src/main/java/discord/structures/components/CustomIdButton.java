package discord.structures.components;

import java.util.Objects;

import sj.SjObject;

public class CustomIdButton extends Button {
	public final String customId;
	public String unicodeEmoji;
	//public Emoji emoji;

	// When receiving
	public CustomIdButton(final SjObject data) {
		super(data);
		customId = Objects.requireNonNull(data.getString("custom_id"));
		try {
			unicodeEmoji = data.getString("emoji");
		} catch (final ClassCastException e) {
		}
	}

	// When sending
	public CustomIdButton(final Style style, final String customId, final String label) {
		super(style);
		this.customId = Objects.requireNonNull(customId);
		this.label = Objects.requireNonNull(label);
	}

	@Override
	public String toJsonString() {
		final var obj = toJSONObject();
		obj.put("custom_id", customId);
		if (unicodeEmoji != null)
			obj.put("emoji", unicodeEmoji);
		return obj.toJsonString();
	}
}
