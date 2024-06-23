package discord.resources;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import discord.client.BotDiscordClient;
import discord.managers.ApplicationCommandManager;
import discord.structures.ApplicationCommandOption;
import sj.SjObject;
import sj.SjSerializable;

/**
 * https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-structure
 */
public class ApplicationCommand extends AbstractDiscordResource {
	/**
	 * https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-types
	 */
	public static enum Type {
		CHAT_INPUT, MESSAGE, USER;

		private static final Type[] LOOKUP_TABLE;

		static {
			final var values = Type.values();
			LOOKUP_TABLE = new Type[values[values.length - 1].value + 1];
			Stream.of(values).forEach(t -> LOOKUP_TABLE[t.value] = t);
		}

		public static Type resolve(int value) {
			return LOOKUP_TABLE[value];
		}

		public final int value;

		private Type() {
			this.value = 1 + ordinal();
		}
	}

	/**
	 * Application command payload. Should be used when creating or editing
	 * application commands using {@link ApplicationCommandManager#create} or
	 * {@link ApplicationCommandManager#edit}.
	 */
	public static class Payload implements SjSerializable {
		/**
		 * Name of command, 1-32 characters
		 */
		private final String name;

		/**
		 * Type of command. When sent to Discord, it defaults to {@link Type#CHAT_INPUT} if not set
		 */
		private final Type type;

		/**
		 * Indicates whether the command is available in DMs with the app, only for
		 * globally-scoped commands. By default, commands are visible. <b>Use only when
		 * creating application commands globally, and not for a guild.</b>
		 */
		public Boolean dmPermission;

		/**
		 * @param type Must be either {@link Type#MESSAGE} or {@link Type#USER}
		 * @param name Name of the command
		 */
		public Payload(Type type, String name) {
			if (type == Type.CHAT_INPUT)
				throw new IllegalArgumentException("Use ChatInputPayload instead");
			this.type = type;
			this.name = Objects.requireNonNull(name);
		}

		// only used by ChatInputPayload
		protected SjObject toSjObject() {
			final var obj = new SjObject();
			obj.put("name", name);
			if (type != null)
				obj.put("type", type.value);
			if (dmPermission != null)
				obj.put("dm_permission", dmPermission);
			return obj;
		}

		@Override
		public String toJsonString() {
			return toSjObject().toJsonString();
		}
	}

	/**
	 * Chat input application command payload.
	 * Has extra attributes specific to chat input commands.
	 */
	public static class ChatInputPayload extends Payload {
		private final String description;

		/**
		 * To add options to this command, assign this member a list.
		 * It is {@code null} by default.
		 */
		public List<ApplicationCommandOption.Payload> options;

		public ChatInputPayload(
			final String name,
			final String description,
			final List<ApplicationCommandOption.Payload> options
		) {
			super(null, name);
			this.description = Objects.requireNonNull(description);
			this.options = options;
		}

		public ChatInputPayload(final String name, final String description) {
			this(name, description, null);
		}

		@Override
		public String toJsonString() {
			final var obj = toSjObject();
			obj.put("description", description);
			if (options != null && options.size() > 0)
				obj.put("options", options);
			return obj.toJsonString();
		}
	}

	/**
	 * Extra reference to the same client but as the desired type. This is better
	 * than having to cast to {@link BotDiscordClient} every time.
	 */
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
