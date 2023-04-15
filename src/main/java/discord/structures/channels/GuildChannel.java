package discord.structures.channels;

import org.json.simple.JSONAware;

import discord.structures.GuildObject;
import simple_json.JSONObject;

public interface GuildChannel extends GuildObject, Channel {

	// public PermissionOverwrites permission_overwrites;

	@Override
	default String url() {
		return "https://discord.com/channels/" + guildId() + '/' + id();
	}

	default Long position() {
		return getData().getLong("position");
	}

	default String parentId() {
		return getData().getString("parent_id");
	}

	default CategoryChannel parent() {
		return (CategoryChannel) client().channels.fetch(parentId()).join();
	}

	// https://discord.com/developers/docs/resources/channel#modify-channel
	// subclasses MUST implement JSONAware
	public abstract static class Payload implements JSONAware {
		public String name;
		public Integer position;
		// permission overwrites

		public JSONObject toJSONObject() {
			final var obj = new JSONObject();

			if (name != null) {
				obj.put("name", name);
			}

			if (position != null) {
				obj.put("position", position);
			}

			// overwrites
			
			return obj;
		}
	}

}
