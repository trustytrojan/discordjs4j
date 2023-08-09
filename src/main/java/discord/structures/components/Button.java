package discord.structures.components;

import java.util.Objects;

import sj.SjObject;

public abstract class Button extends MessageComponent {
	public static enum Style {
		PRIMARY, SECONDARY, SUCCESS, DANGER, LINK;

		public static Style resolve(final int value) {
			return Style.values()[value - 1];
		}

		public int getValue() {
			return (ordinal() + 1);
		}
	}

	/**
	 * Construct a {@link Button} object from a JSON object from Discord.
	 * 
	 * @param data Button object from Discord
	 * @return An instance of a subtype of {@link Button}
	 */
	public static Button construct(final SjObject data) {
		switch (Style.resolve(data.getLong("style").shortValue())) {
			case PRIMARY:
			case SECONDARY:
			case SUCCESS:
			case DANGER:
				return new CustomIdButton(data);
			case LINK:
				return new LinkButton(data);
		}
		return null;
	}

	public final Style style;
	public String label;
	public boolean disabled;

	/**
	 * Constructor to use when viewing data from Discord.
	 * 
	 * @param data Button object from Discord
	 */
	protected Button(final SjObject data) {
		super(data);
		style = Style.resolve(data.getShort("style"));
		label = data.getString("label");
		disabled = data.getBooleanDefaultFalse("disabled");
	}

	/**
	 * Constructor to use when constructing a payload to send to Discord.
	 * 
	 * @param style Style of the button
	 */
	protected Button(final Style style) {
		super(Type.BUTTON);
		this.style = Objects.requireNonNull(style);
	}

	@Override
	public SjObject toJsonObject() {
		final var obj = super.toJsonObject();
		obj.put("style", style.getValue());
		if (label != null)
			obj.put("label", label);
		if (disabled)
			obj.put("disabled", Boolean.TRUE);
		return obj;
	}
}
