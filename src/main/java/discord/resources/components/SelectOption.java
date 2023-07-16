package discord.resources.components;

import java.util.Objects;

import sj.SjObject;
import sj.SjSerializable;

public class SelectOption implements SjSerializable {
	public final String label;
	public final String value;
	public String description;
	public String unicodeEmoji;
	//public Emoji emoji;
	public boolean isDefault;

	public SelectOption(final SjObject data) {
		label = Objects.requireNonNull(data.getString("label"));
		value = Objects.requireNonNull(data.getString("value"));
		description = data.getString("description");
		try {
			unicodeEmoji = data.getString("emoji");
		} catch (final ClassCastException e) {
		}
		isDefault = data.getBooleanDefaultFalse("default");
	}

	public SelectOption(final String label, final String value) {
		this.label = Objects.requireNonNull(label);
		this.value = Objects.requireNonNull(value);
	}

	@Override
	public String toJsonString() {
		final var obj = new SjObject();
		obj.put("label", label);
		obj.put("value", value);
		if (description != null)
			obj.put("description", description);
		if (unicodeEmoji != null)
			obj.put("emoji", unicodeEmoji);
		if (isDefault)
			obj.put("default", Boolean.TRUE);
		return obj.toJsonString();
	}
}
