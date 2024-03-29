package discord.structures.components;

import java.util.Objects;

import sj.SjObject;

public class TextInput extends MessageComponent {
	public static enum Style {
		SHORT, PARAGRAPH;

		public static Style resolve(final int value) {
			return Style.values()[value - 1];
		}

		public int getValue() {
			return (ordinal() + 1);
		}
	}

	public final String customId;
	public final Style style;
	public final String label;
	public Short minLength;
	public Short maxLength;
	public Boolean required;
	public String value;
	public String placeholder;

	public TextInput(final SjObject data) {
		super(data);
		customId = Objects.requireNonNull(data.getString("custom_id"));
		style = Style.resolve(Objects.requireNonNull(data.getShort("style")));
		label = Objects.requireNonNull(data.getString("label"));
		minLength = data.getShort("min_length");
		maxLength = data.getShort("max_length");
		required = data.getBooleanDefaultFalse("required");
		value = data.getString("value");
		placeholder = data.getString("placeholder");
	}
	
	public TextInput(final String customId, final Style style, final String label) {
		super(Type.TEXT_INPUT);
		this.customId = Objects.requireNonNull(customId);
		this.style = Objects.requireNonNull(style);
		this.label = Objects.requireNonNull(label);
	}

	@Override
	public String toJsonString() {
		final var obj = toJsonObject();
		obj.put("custom_id", customId);
		obj.put("style", style.getValue());
		obj.put("label", label);
		if (minLength != null)
			obj.put("min_length", minLength);
		if (maxLength != null)
			obj.put("max_length", maxLength);
		if (required != null)
			obj.put("required", required);
		if (value != null)
			obj.put("value", value);
		if (placeholder != null)
			obj.put("placeholder", placeholder);
		return obj.toJsonString();
	}
}
