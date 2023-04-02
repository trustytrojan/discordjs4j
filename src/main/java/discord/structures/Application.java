package discord.structures;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

import discord.client.DiscordClient;
import simple_json.JSONObject;

public class Application implements DiscordResource {

	private final DiscordClient client;
	private JSONObject data;

	public Application(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}

	public User owner() {
		return client.users.fetch(data.getObject("owner").getString("id"));
	}

	// https://discord.com/developers/docs/resources/application

	@Override
	public DiscordClient client() {
		return client;
	}
	
	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public void setData(JSONObject data) {
		this.data = data;
	}

	public static class Command implements DiscordResource {

		private final DiscordClient.Bot client;
		private JSONObject data;
	
		public Command(DiscordClient.Bot client, JSONObject data) {
			this.client = client;
			this.data = data;
		}
	
		public Type type() {
			return Type.resolve(data.getLong("type"));
		}
	
		public String name() {
			return data.getString("name");
		}
	
		public String description() {
			return data.getString("description");
		}
	
		@Override
		public DiscordClient client() {
			return client;
		}

		@Override
		public JSONObject getData() {
			return data;
		}

		@Override
		public void setData(JSONObject data) {
			this.data = data;
		}
		
		public static class Option {
	
			public static enum Type {
				SUBCOMMAND(1),
				SUBCOMMAND_GROUP(2),
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
				public final ArrayList<Choice> choices = new ArrayList<>();
			
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
						obj.put("choices", (JSONArray) choices);
					}
			
					return obj.toString();
				}
			}
	
		}
	
		public static enum Type {
			CHAT_INPUT(1),
			MESSAGE(2),
			USER(3);
		
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
	
		public static class Payload implements JSONAware {
			public Type type;
			public String name;
			public String description;
			public final ArrayList<Option.Payload> options = new ArrayList<>();
		
			public Payload(Type type, String name, String description) {
				this.type = type;
				this.name = name;
				this.description = description;
			}
		
			public Payload(String name, String description) {
				this(null, name, description);
			}
		
			@Override
			public String toJSONString() {
				final var obj = new JSONObject();
		
				if (type != null) {
					obj.put("type", type.value);
				}
		
				obj.put("name", name);
				obj.put("description", description);
		
				if (options.size() > 0) {
					obj.put("options", (JSONArray) options);
				}
		
				return obj.toString();
			}
		}
	
	}
	
}
