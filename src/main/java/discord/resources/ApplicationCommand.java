package discord.resources;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import discord.client.BotDiscordClient;
import discord.structures.ApplicationCommandOption;
import sj.SjObject;
import sj.SjSerializable;

public class ApplicationCommand extends AbstractDiscordResource {
	public static enum Type {
		CHAT_INPUT(1),
		MESSAGE(2),
		USER(3);

		private static final Type[] LOOKUP_TABLE = new Type[3];

		static {
			Stream.of(Type.values()).forEach(t -> LOOKUP_TABLE[t.value] = t);
		}

		public final int value;

		private Type(int value) {
			this.value = value;
		}
	}

	public static class Payload implements SjSerializable {
		private final String name;
		public final String description;
		public Type type;
		public List<ApplicationCommandOption.Payload> options;
		public boolean dmPermission;

		public Payload(String name, String description) {
			this.name = name;
			this.description = description;
		}

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
			if (dmPermission)
				obj.put("dm_permission", Boolean.TRUE);
			return obj.toJsonString();
		}
	}

	private final BotDiscordClient client;

	public ApplicationCommand(BotDiscordClient client, SjObject data) {
		super(client, data);
		this.client = client;
	}

	@Override
	public String getApiPath() {
		return "/application/" + client.application.getId() + "/commands/" + getId();
	}

	public CompletableFuture<ApplicationCommand> edit(Payload payload) {
		return client.application.commands.edit(getId(), payload);
	}

	public CompletableFuture<Void> delete() {
		return client.application.commands.delete(getId());
	}

	public List<ApplicationCommandOption> getOptions() {
		final var rawOptions = data.getObjectArray("options");
		return (rawOptions == null)
			? Collections.emptyList()
			: rawOptions.stream().map(ApplicationCommandOption::new).toList();
	}

	public Type getType() {
		return Type.LOOKUP_TABLE[data.getInteger("type")];
	}

	public String getName() {
		return data.getString("name");
	}

	public String getDescription() {
		return data.getString("description");
	}
}
