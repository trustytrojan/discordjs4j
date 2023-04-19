package discord.structures.components;

import java.util.Objects;

import org.json.simple.JSONAware;

import simple_json.JSONObject;

public class SelectOption implements JSONAware {
	public final String label;
	public final String value;
	public String description;
	public String unicodeEmoji;
	//public Emoji emoji;
	public boolean isDefault;

	public SelectOption(final JSONObject data) {
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
	public String toJSONString() {
		final var obj = new JSONObject();
		obj.put("label", label);
		obj.put("value", value);
		if (description != null)
			obj.put("description", description);
		if (unicodeEmoji != null)
			obj.put("emoji", unicodeEmoji);
		if (isDefault)
			obj.put("default", Boolean.TRUE);
		return obj.toJSONString();
	}
}
