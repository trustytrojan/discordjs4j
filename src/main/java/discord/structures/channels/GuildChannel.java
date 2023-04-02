package discord.structures.channels;

import discord.structures.GuildObject;

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
		return (CategoryChannel) client().channels.fetch(parentId());
	}

}
