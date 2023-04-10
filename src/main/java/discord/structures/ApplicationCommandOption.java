package discord.structures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONAware;

import simple_json.JSONObject;

public final class ApplicationCommandOption {

	public final Type type;
	public final String name;
	public final String description;
	public final boolean required;
	private final List<Choice> choices = new ArrayList<>();

	public ApplicationCommandOption(JSONObject data) {
		type = Type.resolve(data.getLong("type"));
		name = data.getString("name");
		description = data.getString("description");
		final var r = data.getBoolean("required");
		required = (r == null) ? false : r;

		final var rawChoices = data.getObjectArray("choices");
		if (rawChoices != null)
			for (final var rawChoice : rawChoices)
				choices.add(new Choice(rawChoice));
	}

	public Iterable<Choice> choices() {
		final var itr = choices.iterator();
		return new Iterable<>() {
			@Override
			public Iterator<Choice> iterator() {
				return new Iterator<>() {
					@Override
					public boolean hasNext() {
						return itr.hasNext();
					}
					@Override
					public Choice next() {
						return itr.next();
					}
				};
			}
		};
	}

	public enum Type {
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

		@Override
		public String toString() {
			return switch (this) {
				case SUB_COMMAND -> "Subcommand";
				case SUB_COMMAND_GROUP -> "Subcommand Group";
				case STRING -> "String";
				case INTEGER -> "Integer";
				case BOOLEAN -> "Boolean";
				case USER -> "User";
				case CHANNEL -> "Channel";
				case ROLE -> "Role";
				case MENTIONABLE -> "Mentionable";
				case NUMBER -> "Number";
				case ATTACHMENT -> "Attachment";
			};
		}
	}

	public static class Choice implements JSONAware {
		public final String name;
		public final Object value;

		/**
		 * {@code value} should be either a {@code String} or a {@code Number}.
		 */
		private Choice(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		private Choice(JSONObject data) {
			this(data.getString("name"), data.get("value"));
		}

		@Override
		public String toJSONString() {
			return """
					{
						"name": "%s",
						"value": "%s"
					}
					""".formatted(name, value);
		}
	}

	public static class Payload implements JSONAware {
		public Type type;
		public String name;
		public String description;
		public boolean required;
		private final List<Choice> choices = new LinkedList<>();

		public Payload(Type type, String name, String description, boolean required) {
			this.type = type;
			this.name = name;
			this.description = description;
			this.required = required;
		}

		public Payload(Type type, String name, String description) {
			this(type, name, description, false);
		}

		/**
		 * @param name  Name of the choice
		 * @param value Value of the choice - should only be either a {@code String} or
		 *              a {@code Number}
		 */
		public void addChoice(String name, Object value) {
			choices.add(new Choice(name, value));
		}

		@Override
		public String toJSONString() {
			final var obj = new JSONObject();

			obj.put("type", type.value);
			obj.put("name", name);
			obj.put("description", description);

			if (required) {
				obj.put("required", required);
			}

			if (choices.size() > 0) {
				obj.put("choices", choices);
			}

			return obj.toString();
		}
	}
}