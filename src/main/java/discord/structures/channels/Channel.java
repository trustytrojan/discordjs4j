package discord.structures.channels;

import java.util.concurrent.CompletableFuture;

import discord.structures.DiscordResource;

public interface Channel extends DiscordResource {
	String url();

	default String mention() {
		return "<#" + id() + '>';
	}

	default String name() {
		return getData().getString("name");
	}

	default Type type() {
		return Type.resolve(getData().getLong("type"));
	}

	default CompletableFuture<Void> delete() {
		return client().channels.delete(id());
	}

	@Override
	default String apiPath() {
		return "/channels/" + id();
	}

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

		public final int value;

		private Type(int value) {
			this.value = value;
		}

		public static Type resolve(long value) {
			for (final var x : Type.values())
				if (x.value == value)
					return x;
			return null;
		}
	}
}
