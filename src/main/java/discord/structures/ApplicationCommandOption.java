package discord.structures;

import java.util.ArrayList;
import java.util.Collections;
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

	public List<Choice> choices() {
		return Collections.unmodifiableList(choices);
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

		@Override
		public String toString() {
			return toJSONString();
		}
	}

	public static class Payload implements JSONAware {
		private Type type;
		private String name;
		private String description;
		private boolean required;
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

		@Override
		public String toString() {
			return toJSONString();
		}
	}
}