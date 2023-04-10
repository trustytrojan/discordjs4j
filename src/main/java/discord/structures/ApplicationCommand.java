package discord.structures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONAware;

import discord.client.BotDiscordClient;
import discord.client.DiscordClient;
import discord.util.Util;
import simple_json.JSONObject;

public class ApplicationCommand implements DiscordResource {

	private final BotDiscordClient client;
	private JSONObject data;
	private final List<ApplicationCommandOption> options = new ArrayList<>();

	public ApplicationCommand(BotDiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;

		final var rawOptions = data.getObjectArray("options");
		if (rawOptions != null)
			for (final var rawOption : rawOptions)
				options.add(new ApplicationCommandOption(rawOption));
	}

	public Iterable<ApplicationCommandOption> options() {
		return Util.constView(options);
	}

	@Override
	public String toString() {
		return getData().toString();
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

		@Override
		public String toString() {
			return switch (this) {
				case CHAT_INPUT -> "Chat Input";
				case MESSAGE -> "Message";
				case USER -> "User";
			};
		}
	}

	public static class Payload implements JSONAware {
		public Type type;
		public String name;
		public String description;
		private final List<ApplicationCommandOption.Payload> options = new LinkedList<>();

		public Payload(Type type, String name, String description) {
			this.type = type;
			this.name = name;
			this.description = description;
		}

		public Payload(String name, String description) {
			this(null, name, description);
		}

		public void addOption(ApplicationCommandOption.Type type, String name, String description, boolean required) {
			options.add(new ApplicationCommandOption.Payload(type, name, description, required));
		}

		public void addOption(ApplicationCommandOption.Type type, String name, String description) {
			options.add(new ApplicationCommandOption.Payload(type, name, description));
		}

		public void addOption(ApplicationCommandOption.Payload option) {
			options.add(option);
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