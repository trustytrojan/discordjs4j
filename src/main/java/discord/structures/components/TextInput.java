package discord.structures.components;

import java.util.Objects;

import simple_json.JSONObject;

public class TextInput extends MessageComponent {
	public static enum Style {
		SHORT, PARAGRAPH;

		public static Style resolve(final short value) {
			return Style.values()[value - 1];
		}

		public short value() {
			return (short) (ordinal() + 1);
		}
	}

	public final String customId;
	public final Style style;
	public final String label;
	public Short minLength;
	public Short maxLength;
	public boolean required;
	public String value;
	public String placeholder;

	public TextInput(final JSONObject data) {
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
	public String toJSONString() {
		final var obj = toJSONObject();
		obj.put("custom_id", customId);
		obj.put("style", style.value());
		obj.put("label", label);
		if (minLength != null)
			obj.put("min_length", minLength);
		if (maxLength != null)
			obj.put("max_length", maxLength);
		if (required)
			obj.put("required", Boolean.TRUE);
		if (value != null)
			obj.put("value", value);
		if (placeholder != null)
			obj.put("placeholder", placeholder);
		return obj.toJSONString();
	}
}
