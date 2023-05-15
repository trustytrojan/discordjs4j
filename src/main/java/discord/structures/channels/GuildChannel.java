package discord.structures.channels;

import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONAware;

import discord.structures.GuildResource;
import simple_json.SjObject;

public interface GuildChannel extends GuildResource, Channel {
	// public PermissionOverwrites permission_overwrites;

	@Override
	default String url() {
		return "https://discord.com/channels/" + guildId() + '/' + id();
	}

	default Integer position() {
		return getData().getInt("position");
	}

	default String parentId() {
		return getData().getString("parent_id");
	}

	default CompletableFuture<CategoryChannel> fetchParent() {
		final var parentId = parentId();
		if (parentId == null)
			return CompletableFuture.completedFuture(null);
		return client().channels.fetch(parentId()).thenApplyAsync(c -> (CategoryChannel) c);
	}

	// https://discord.com/developers/docs/resources/channel#modify-channel
	public abstract class Payload implements JSONAware {
		public final String name;
		// permission overwrites

		protected Payload(String name) {
			this.name = name;
		}

		public SjObject toJSONObject() {
			final var obj = new SjObject();
			if (name != null)
				obj.put("name", name);
			// overwrites
			return obj;
		}
	}

	public class PositionPayload implements JSONAware {
		private final String id;
		public Integer position;
		public boolean syncPermissions;
		public String parentId;

		public PositionPayload(String id) {
			this.id = id;
		}

		@Override
		public String toJSONString() {
			final var obj = new SjObject();
			obj.put("id", id);
			if (position != null)
				obj.put("position", position);
			if (syncPermissions)
				obj.put("lock_permissions", Boolean.TRUE);
			if (parentId != null)
				obj.put("parent_id", parentId);
			return obj.toJSONString();
		}
	}

	default PositionPayload toPositionPayload() {
		final var payload = new PositionPayload(id());
		payload.position = position();
		payload.parentId = parentId();
		return payload;
	}
}
