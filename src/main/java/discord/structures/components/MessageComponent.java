package discord.structures.components;

import java.util.Objects;

import sj.SjObject;
import sj.SjSerializable;

public abstract class MessageComponent implements SjSerializable {
	public static enum Type {
		ACTION_ROW,
		BUTTON,
		STRING_SELECT,
		TEXT_INPUT,
		USER_SELECT,
		ROLE_SELECT,
		MENTIONABLE_SELECT,
		CHANNEL_SELECT;

		public static Type resolve(final int value) {
			return Type.values()[value - 1];
		}

		public int getValue() {
			return ordinal() + 1;
		}
	}

	public static MessageComponent construct(final SjObject data) {
		switch (Type.resolve(data.getShort("type"))) {
			case ACTION_ROW:
				return new ActionRow(data);
			case BUTTON:
				return Button.construct(data);
			case STRING_SELECT:
			case USER_SELECT:
			case ROLE_SELECT:
			case MENTIONABLE_SELECT:
			case CHANNEL_SELECT:
				return new SelectMenu(data);
			case TEXT_INPUT:
				return new TextInput(data);
		}
		return null;
	}

	public final Type type;

	// When parsing Sj from Discord
	protected MessageComponent(final SjObject data) {
		type = Type.resolve(data.getShort("type"));
	}

	// When sending a message
	protected MessageComponent(final Type type) {
		this.type = Objects.requireNonNull(type);
	}

	protected SjObject toJsonObject() {
		final var obj = new SjObject();
		obj.put("type", type.getValue());
		return obj;
	}
}
