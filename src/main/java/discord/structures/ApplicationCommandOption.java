package discord.structures;

import java.util.Collections;
import java.util.List;

import sj.SjObject;
import sj.SjSerializable;

public final class ApplicationCommandOption {
	public static enum Type {
		SUB_COMMAND,
		SUB_COMMAND_GROUP,
		STRING,
		INTEGER,
		BOOLEAN,
		USER,
		CHANNEL,
		ROLE,
		MENTIONABLE,
		NUMBER,
		ATTACHMENT;

		public static Type resolve(final int value) {
			return Type.values()[value - 1];
		}

		public int getValue() {
			return 1 + ordinal();
		}
	}

	public static class Choice implements SjSerializable {
		public final String name;
		public final Object value;

		/**
		 * {@code value} should be either a {@code String} or a {@code Number}.
		 */
		private Choice(final String name, final Object value) {
			this.name = name;
			this.value = value;
		}

		private Choice(final SjObject data) {
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
	}

	public static abstract class Payload implements SjSerializable {
		public final Type type;
		public final String name;
		public final String description;

		protected Payload(final Type type, final String name, final String description) {
			this.type = type;
			this.name = name;
			this.description = description;
		}

		public SjObject toSjObject() {
			final var obj = new SjObject();
			obj.put("type", type.getValue());
			obj.put("name", name);
			obj.put("description", description);
			return obj;
		}
	}

	public static class NonSubcommandPayload extends Payload {
		public Boolean required;
		public List<Choice> choices;

		public NonSubcommandPayload(final Type type, final String name, final String description, final boolean required) {
			super(type, name, description);
			if (type == Type.SUB_COMMAND)
				throw new IllegalArgumentException("SUBCOMMAND type passed to NonSubcommandPayload");
			this.required = required;
		}

		public NonSubcommandPayload(final Type type, final String name, final String description) {
			this(type, name, description, false);
		}

		@Override
		public String toJsonString() {
			final var obj = toSjObject();
			if (required != null)
				obj.put("required", required);
			if (choices != null && choices.size() > 0)
				obj.put("choices", choices);
			return obj.toJsonString();
		}
	}

	public static class SubcommandPayload extends Payload {
		public List<Payload> options;

		public SubcommandPayload(final String name, final String description, final List<Payload> options) {
			super(Type.SUB_COMMAND, name, description);
			this.options = options;
		}

		public SubcommandPayload(final String name, final String description) {
			this(name, description, null);
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

	public ApplicationCommandOption(final SjObject data) {
		type = Type.resolve(data.getInteger("type"));
		name = data.getString("name");
		description = data.getString("description");
		required = data.getBooleanDefaultFalse("required");

		final var rawChoices = data.getObjectArray("choices");
		choices = (rawChoices == null)
			? Collections.emptyList()
			: rawChoices.stream().map(Choice::new).toList();

		final var rawOptions = data.getObjectArray("options");
		options = (rawOptions == null)
			? Collections.emptyList()
			: rawOptions.stream().map(ApplicationCommandOption::new).toList();
	}
}
