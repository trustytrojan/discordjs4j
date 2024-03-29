package discord.structures.components;

import java.util.Objects;

import sj.SjObject;

public class SelectMenu extends MessageComponent {
	public final String customId;
	public String placeholder;
	public Short minValues;
	public Short maxValues;
	public Boolean disabled;

	public SelectMenu(final SjObject data) {
		super(data);
		customId = Objects.requireNonNull(data.getString("custom_id"));
		placeholder = data.getString("placeholder");
		disabled = data.getBooleanDefaultFalse("disabled");
		minValues = data.getShort("min_values");
		maxValues = data.getShort("max_values");
	}

	/**
	 * Construct a {@link SelectMenu} to use as a message component payload.
	 * @param type Type of the select menu
	 * @param customId
	 */
	public SelectMenu(final Type type, final String customId) {
		super(type);
		this.customId = Objects.requireNonNull(customId);
	}

	@Override
	public String toJsonString() {
		final var obj = toJsonObject();
		obj.put("custom_id", customId);
		if (placeholder != null)
			obj.put("placeholder", placeholder);
		if (disabled != null)
			obj.put("disabled", disabled);
		if (minValues != null)
			obj.put("min_values", minValues);
		if (maxValues != null)
			obj.put("max_values", maxValues);
		return obj.toJsonString();
	}
}
