package discord.resources;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import discord.client.BotDiscordClient;
import sj.SjObject;
import sj.SjSerializable;

public class ApplicationCommand extends AbstractDiscordResource {
	public static enum Type {
		CHAT_INPUT(1),
		MESSAGE(2),
		USER(3);

		public static final Type[] TYPE_TABLE = new Type[4];

		static {
			Stream.of(Type.values())
				.forEach(t -> TYPE_TABLE[t.value] = t);
		}

		public final short value;

		private Type(int value) {
			this.value = (short) value;
		}
	}

	private final BotDiscordClient client;
	private List<ApplicationCommandOption> options;

	public ApplicationCommand(BotDiscordClient client, SjObject data) {
		super(client, data);
		this.client = client;
	}

	public List<ApplicationCommandOption> options() {
		return options;
	}

	public Type type() {
		return Type.TYPE_TABLE[data.getShort("type")];
	}

	public String name() {
		return data.getString("name");
	}

	public String description() {
		return data.getString("description");
	}

	@Override
	public void setData(SjObject data) {
		this.data = data;
		final var rawOptions = data.getObjectArray("options");
		options = (rawOptions == null)
				? Collections.emptyList()
				: rawOptions.stream().map(ApplicationCommandOption::new).toList();
	}

	@Override
	public String apiPath() {
		return "/applications/" + client.application.id + '/' + id;
	}

	public static class Payload implements SjSerializable {
		public Type type;
		public String name;
		public String description;
		public List<ApplicationCommandOption.Payload> options;

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			obj.put("name", name);
			if (type != null)
				obj.put("type", type.value);
			if (description != null)
				obj.put("description", description);
			if (options != null && options.size() > 0)
				obj.put("options", options);
			return obj.toJsonString();
		}
	}
}
