package discord.resources;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import sj.SjObject;
import sj.SjSerializable;

public class ApplicationCommand extends AbstractDiscordResource {
	public static enum Type {
		CHAT_INPUT,
		MESSAGE,
		USER;

		public static Type resolve(SjObject data) {
			return switch (data.getInteger("type")) {
				case 1 -> CHAT_INPUT;
				case 2 -> MESSAGE;
				case 3 -> USER;
				default -> null;
			};
		}

		public final int value;

		private Type() {
			value = ordinal() + 1;
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
	private List<ApplicationCommandOption> options;

	public ApplicationCommand(BotDiscordClient client, SjObject data) {
		super(client, data, "/applications/" + client.application.id + "/commands");
		this.client = client;
	}

	public CompletableFuture<ApplicationCommand> edit(Payload payload) {
		return client.application.commands.edit(id, payload);
	}

	public CompletableFuture<Void> delete() {
		return client.application.commands.delete(id);
	}

	public List<ApplicationCommandOption> getOptions() {
		return Collections.unmodifiableList(options);
	}

	public Type getType() {
		return Type.resolve(data);
	}

	public String getName() {
		return data.getString("name");
	}

	public String getDescription() {
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
}
