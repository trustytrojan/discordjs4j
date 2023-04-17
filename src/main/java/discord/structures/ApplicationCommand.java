package discord.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONAware;

import discord.client.BotDiscordClient;
import discord.client.DiscordClient;
import simple_json.JSONObject;

public class ApplicationCommand implements DiscordResource {
	private final BotDiscordClient client;
	private JSONObject data;

	private final List<ApplicationCommandOption> options = new ArrayList<>();

	public ApplicationCommand(final BotDiscordClient client, final JSONObject data) {
		this.client = client;
		setData(data);
	}

	public List<ApplicationCommandOption> options() {
		return Collections.unmodifiableList(options);
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

	public CompletableFuture<ApplicationCommand> edit(Payload payload) {
		return client.commands.edit(id(), payload);
	}

	public CompletableFuture<Void> delete() {
		return client.commands.delete(id());
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
	public void setData(final JSONObject data) {
		this.data = data;
		options.clear();
		final var rawOptions = data.getObjectArray("options");
		if (rawOptions != null) {
			for (final var rawOption : rawOptions) {
				options.add(new ApplicationCommandOption(rawOption));
			}
		}
	}

	@Override
	public String apiPath() {
		return "/applications/" + client.application.id() + '/' + id();
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
		private final List<ApplicationCommandOption.Payload> options = new LinkedList<>();

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

			if (description != null) {
				obj.put("description", description);
			}

			if (options.size() > 0) {
				obj.put("options", options);
			}

			return obj.toString();
		}

		@Override
		public String toString() {
			return toJSONString();
		}
	}
}
