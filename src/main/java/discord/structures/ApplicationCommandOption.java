package discord.structures;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONAware;

import simple_json.JSONObject;

public final class ApplicationCommandOption {

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

	/**
	 * {@code value} should be either a {@code String} or a {@code Number}.
	 */
	public static record Choice(String name, Object value) implements JSONAware {
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
		public final List<Choice> choices = new LinkedList<>();
	
		public Payload(Type type, String name, String description, boolean required) {
			this.type = type;
			this.name = name;
			this.description = description;
			this.required = required;
		}
	
		public Payload(Type type, String name, String description) {
			this(type, name, description, false);
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