package discord.resources.channels;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import discord.client.DiscordClient;
import discord.resources.DiscordResource;
import sj.SjObject;

public interface Channel extends DiscordResource {
	public static enum Type {
		GUILD_TEXT,
		DM,
		GUILD_VOICE,
		GROUP_DM,
		GUILD_CATEGORY,
		GUILD_ANNOUNCEMENT,
		ANNOUNCEMENT_THREAD(10),
		PUBLIC_THREAD(11),
		PRIVATE_THREAD(12),
		GUILD_STAGE_VOICE(13),
		GUILD_DIRECTORY(14),
		GUILD_FORUM(15),
		GUILD_MEDIA(16);

		private static final Type[] LOOKUP_TABLE = new Type[17];

		static {
			Stream.of(Type.values()).forEach(t -> LOOKUP_TABLE[t.value] = t);
		}

		public final int value;

		private Type() {
			value = ordinal();
		}

		private Type(final int value) {
			this.value = value;
		}
	}

	public static Channel construct(final DiscordClient client, final SjObject data) {
		return switch (Type.LOOKUP_TABLE[data.getInteger("type")]) {
			case GUILD_TEXT -> new TextChannel(client, data);
			case DM -> new DMChannel(client, data);
			case GUILD_VOICE -> new VoiceChannel(client, data);
			case GROUP_DM -> new GroupDMChannel(client, data);
			case GUILD_CATEGORY -> new CategoryChannel(client, data);
			// ...
			default -> null;
		};
	}

	@Override
	default String getApiPath() {
		return "/channels/" + getId();
	}

	String getUrl();

	default String getName() {
		return getData().getString("name");
	}

	default Type getType() {
		return Type.LOOKUP_TABLE[getData().getInteger("type")];
	}

	/**
	 * Based on the type of this channel, performs one of the following three actions:
	 * <ul>
	 * <li>Delete a guild channel.</li>
	 * <li>Close a DM channel.</li>
	 * <li>Leave a group DM channel.</li>
	 * </ul>
	 */
	default CompletableFuture<Void> delete() {
		return getClient().channels.delete(getId());
	}
}
