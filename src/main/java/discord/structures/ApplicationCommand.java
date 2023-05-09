package discord.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONAware;

import discord.client.BotDiscordClient;
import discord.client.DiscordClient;
import simple_json.SjObject;

public class ApplicationCommand implements DiscordResource {
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

	private final BotDiscordClient client;
	private SjObject data;

	private final List<ApplicationCommandOption> options = new ArrayList<>();

	public ApplicationCommand(final BotDiscordClient client, final SjObject data) {
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
	public SjObject getData() {
		return data;
	}

	@Override
	public void setData(final SjObject data) {
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

	public static class Payload implements JSONAware {
		public Type type;
		public String name;
		public String description;
		public List<ApplicationCommandOption.Payload> options;

		@Override
		public String toJSONString() {
			final var obj = new SjObject();
			obj.put("name", name);
			if (type != null)
				obj.put("type", type.value);
			if (description != null)
				obj.put("description", description);
			if (options != null && options.size() > 0)
				obj.put("options", options);
			return obj.toJSONString();
		}
	}
}
