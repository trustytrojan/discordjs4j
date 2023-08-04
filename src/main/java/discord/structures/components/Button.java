package discord.structures.components;

import java.util.Objects;

import sj.SjObject;

public abstract class Button extends MessageComponent {
	public static enum Style {
		PRIMARY, SECONDARY, SUCCESS, DANGER, LINK;

		public static Style resolve(final short value) {
			return Style.values()[value - 1];
		}

		public short value() {
			return (short) (ordinal() + 1);
		}
	}

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

	public Button(final SjObject data) {
		super(data);
		style = Style.resolve(Objects.requireNonNull(data.getShort("style")));
		label = data.getString("label");
		disabled = data.getBooleanDefaultFalse("disabled");
	}

	public Button(final Style style) {
		super(Type.BUTTON);
		this.style = Objects.requireNonNull(style);
	}

	@Override
	public SjObject toJsonObject() {
		final var obj = super.toJsonObject();
		obj.put("style", style.value());
		if (label != null)
			obj.put("label", label);
		if (disabled)
			obj.put("disabled", Boolean.TRUE);
		return obj;
	}
}
