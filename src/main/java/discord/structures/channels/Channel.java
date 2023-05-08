package discord.structures.channels;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import discord.client.DiscordClient;
import discord.structures.DiscordResource;
import simple_json.JSONObject;

public interface Channel extends DiscordResource {
	public static enum Type {
		GUILD_TEXT(0),
		DM(1),
		GUILD_VOICE(2),
		GROUP_DM(3),
		GUILD_CATEGORY(4),
		GUILD_ANNOUNCEMENT(5),
		ANNOUNCEMENT_THREAD(10),
		PUBLIC_THREAD(11),
		PRIVATE_THREAD(12),
		GUILD_STAGE_VOICE(13),
		GUILD_DIRECTORY(14),
		GUILD_FORUM(15);

		public static Type resolve(final short value) {
			return Stream.of(Type.values())
					.filter((final var t) -> t.value == value)
					.findFirst()
					.orElse(null);
		}

		public final short value;

		private Type(final int value) {
			this.value = (short) value;
		}
	}

	public static Channel fromJSON(final DiscordClient client, final JSONObject data) {
		return switch (Type.resolve(data.getShort("type"))) {
			case GUILD_TEXT -> new TextChannel(client, data);
			case DM -> new DMChannel(client, data);
			case GROUP_DM -> new GroupDMChannel(client, data);
			case GUILD_CATEGORY -> new CategoryChannel(client, data);
			// ...
			default -> null;
		};
	}

	String url();

	default String mention() {
		return "<#" + id() + '>';
	}

	default String name() {
		return getData().getString("name");
	}

	default Type type() {
		return Type.resolve(getData().getShort("type"));
	}

	default CompletableFuture<Void> delete() {
		return client().channels.delete(id());
	}

	@Override
	default String apiPath() {
		return "/channels/" + id();
	}
}
