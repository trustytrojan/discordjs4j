package discord.structures;

import java.util.List;

import sj.SjObject;
import sj.SjSerializable;

public final class ApplicationCommandOption {
	public static enum Type {
		SUB_COMMAND(1),
		SUB_COMMAND_GROUP(2),
		STRING(3),
		INTEGER(4),
		BOOLEAN(5),
		USER(6),
		CHANNEL(7),
		ROLE(8),
		MENTIONABLE(9),
		NUMBER(10),
		ATTACHMENT(11);

		public static Type resolve(long value) {
			for (final var x : Type.values())
				if (x.value == value)
					return x;
			return null;
		}

		public final int value;

		private Type(int value) {
			this.value = value;
		}
	}

	public static class Choice implements SjSerializable {
		public final String name;
		public final Object value;

		/**
		 * {@code value} should be either a {@code String} or a {@code Number}.
		 */
		private Choice(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		private Choice(SjObject data) {
			this(data.getString("name"), data.get("value"));
		}

		@Override
		public String toJsonString() {
			return """
					{
						"name": "%s",
						"value": "%s"
					}
					""".formatted(name, value);
		}

		@Override
		public String toString() {
			return toJsonString();
		}
	}

	public static abstract class Payload implements SjSerializable {
		public final Type type;
		public final String name;
		public final String description;

		protected Payload(Type type, String name, String description) {
			this.type = type;
			this.name = name;
			this.description = description;
		}

		public SjObject toSjObject() {
			final var obj = new SjObject();
			obj.put("type", type.value);
			obj.put("name", name);
			obj.put("description", description);
			return obj;
		}
	}

	public static class NonSubcommandPayload extends Payload {
		public boolean required;
		public List<Choice> choices;

		public NonSubcommandPayload(Type type, String name, String description) {
			super(type, name, description);
			if (type == Type.SUB_COMMAND)
				throw new RuntimeException();
		}

		@Override
		public String toJsonString() {
			final var obj = toSjObject();
			if (required)
				obj.put("required", Boolean.TRUE);
			if (choices != null && choices.size() > 0)
				obj.put("choices", choices);
			return obj.toJsonString();
		}
	}

	public static class SubcommandPayload extends Payload {
		public List<Payload> options;

		public SubcommandPayload(String name, String description) {
			super(Type.SUB_COMMAND, name, description);
		}

		@Override
		public String toJsonString() {
			final var obj = toSjObject();
			if (options != null && options.size() > 0)
				obj.put("options", options);
			return obj.toJsonString();
		}
	}

	public final Type type;
	public final String name;
	public final String description;
	public final boolean required;
	public final List<Choice> choices;
	public final List<ApplicationCommandOption> options;

	public ApplicationCommandOption(SjObject data) {
		type = Type.resolve(data.getLong("type"));
		name = data.getString("name");
		description = data.getString("description");
		final var r = data.getBoolean("required");
		required = (r == null) ? false : r;

		final var rawChoices = data.getObjectArray("choices");
		choices = (rawChoices == null)
				? null
				: rawChoices.stream().map(Choice::new).toList();

		final var rawOptions = data.getObjectArray("options");
		options = (rawOptions == null)
				? null
				: rawOptions.stream().map(ApplicationCommandOption::new).toList();
	}
}
