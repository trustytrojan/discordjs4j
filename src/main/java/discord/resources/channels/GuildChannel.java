package discord.resources.channels;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.GuildResource;
import discord.resources.guilds.Guild;
import sj.SjObject;
import sj.SjSerializable;

public interface GuildChannel extends GuildResource, Channel {
	/**
	 * Construct a {@link GuildChannel} with a {@link Guild}.
	 * @param client The calling client
	 * @param data Channel data from Discord.
	 * @param guild The channel's guild. Cannot be {@code null}.
	 * @return An instance of a {@link GuildChannel} subclass decided by the {@code type} property in {@code data},
	 *         or {@code null} if the channel type hasn't been implemented.
	 */
	public static GuildChannel construct(final DiscordClient client, final SjObject data, final Guild guild) {
		Objects.requireNonNull(guild);
		return switch (Channel.Type.LOOKUP_TABLE[data.getInteger("type")]) {
			case GUILD_TEXT -> new TextChannel(client, data, guild);
			case GUILD_VOICE -> new VoiceChannel(client, data, guild);
			case GUILD_CATEGORY -> new CategoryChannel(client, data, guild);
			case GUILD_ANNOUNCEMENT -> new AnnouncementChannel(client, data, guild);
			case GUILD_FORUM -> new ForumChannel(client, data, guild);
			// ...
			default -> null;
		};
	}

	// https://discord.com/developers/docs/resources/channel#modify-channel
	public abstract class Payload implements SjSerializable {
		// subclasses can hardcode the channel type!
		public final String name;
		public Integer position;
		// permission overwrites

		protected Payload(String name) {
			this.name = name;
		}

		protected SjObject toSjObject() {
			final var obj = new SjObject();
			obj.put("name", name);
			if (position != null)
				obj.put("position", position);
			// overwrites
			return obj;
		}
	}

	public class PositionPayload implements SjSerializable {
		private final String id;
		public Integer position;
		public boolean syncPermissions;
		public String parentId;

		public PositionPayload(String id) {
			this.id = id;
		}

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			obj.put("id", id);
			if (position != null)
				obj.put("position", position);
			if (syncPermissions)
				obj.put("lock_permissions", true);
			if (parentId != null)
				obj.put("parent_id", parentId);
			return obj.toJsonString();
		}
	}

	default PositionPayload toPositionPayload() {
		final var payload = new PositionPayload(getId());
		payload.position = getPosition();
		payload.parentId = getParentId();
		return payload;
	}

	@Override
	default String getUrl() {
		return "https://discord.com/channels/" + getGuildId() + '/' + getId();
	}

	default String getGuildId() {
		return getData().getString("guild_id");
	}

	default Integer getPosition() {
		return getData().getInteger("position");
	}

	default String getParentId() {
		return getData().getString("parent_id");
	}

	default boolean hasParent() {
		return getParentId() != null;
	}

	default CompletableFuture<CategoryChannel> getParent() {
		final var id = getParentId();
		return (id == null)
			? CompletableFuture.completedFuture(null)
			: getClient().channels.get(id).thenApply(c -> (CategoryChannel) c);
	}

	/**
	 * Edit this channel using the data in {@code payload}. If successful, changes
	 * will be reflected in {@code this}.
	 * 
	 * @param payload The data to change in this channel
	 * @return 
	 */
	default CompletableFuture<Void> edit(Payload payload) {
		return getGuild().thenAccept(g -> g.channels.edit(getId(), payload));
	}
}
