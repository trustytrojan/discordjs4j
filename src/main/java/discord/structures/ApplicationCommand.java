package discord.structures;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONAware;

import discord.client.DiscordClient;
import simple_json.JSONObject;

public class ApplicationCommand implements DiscordResource {
	
	private final DiscordClient.Bot client;
	private JSONObject data;

	public ApplicationCommand(DiscordClient.Bot client, JSONObject data) {
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
		public final List<ApplicationCommandOption.Payload> options = new LinkedList<>();

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
				obj.put("options", options);
			}

			return obj.toString();
		}
	}

}
