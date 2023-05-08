package discord.structures;

import java.util.List;

import org.json.simple.JSONAware;

import simple_json.JSONObject;

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

	public static abstract class Payload implements JSONAware {
		public Type type;
		public String name;
		public String description;

		public JSONObject toJSONObject() {
			final var obj = new JSONObject();
			obj.put("type", type.value);
			obj.put("name", name);
			obj.put("description", description);
			return obj;
		}
	}

	public static class NonSubcommandPayload extends Payload {
		public boolean required;
		public List<Choice> choices;

		@Override
		public String toJSONString() {
			final var obj = toJSONObject();
			if (required)
				obj.put("required", Boolean.TRUE);
			if (choices != null && choices.size() > 0)
				obj.put("choices", choices);
			return obj.toJSONString();
		}
	}

	public static class SubcommandPayload extends Payload {
		public List<Payload> options;

		@Override
		public String toJSONString() {
			final var obj = toJSONObject();
			if (options != null && options.size() > 0)
				obj.put("options", options);
			return obj.toJSONString();
		}
	}

	public final Type type;
	public final String name;
	public final String description;
	public final boolean required;
	public final List<Choice> choices;
	public final List<ApplicationCommandOption> options;

	public ApplicationCommandOption(JSONObject data) {
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
