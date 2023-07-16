package discord.resources.channels;

import java.util.concurrent.CompletableFuture;

import discord.resources.GuildResource;
import discord.util.Util;
import sj.SjObject;
import sj.SjSerializable;

public interface GuildChannel extends GuildResource, Channel {
	// public PermissionOverwrites permission_overwrites;

	default Integer position() {
		return getData().getInteger("position");
	}

	default String parentId() {
		return getData().getString("parent_id");
	}

	default CompletableFuture<CategoryChannel> parent() {
		final var parentId = parentId();
		return (parentId == null)
			? CompletableFuture.completedFuture(null)
			: client().channels.fetch(parentId).thenApply(c -> (CategoryChannel) c);
	}

	/**
	 * Edit this channel using the data in {@code payload}. If successful, changes
	 * will be reflected in {@code this}.
	 * 
	 * @param payload The data to change in this channel
	 */
	default CompletableFuture<Void> edit(Payload payload) {
		return guild().channels.edit(id(), payload).thenRun(Util.NO_OP);
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
				obj.put("lock_permissions", Boolean.TRUE);
			if (parentId != null)
				obj.put("parent_id", parentId);
			return obj.toJsonString();
		}
	}

	default PositionPayload toPositionPayload() {
		final var payload = new PositionPayload(id());
		payload.position = position();
		payload.parentId = parentId();
		return payload;
	}
}
