package discord.structures.channels;

import discord.structures.GuildObject;

public interface GuildChannel extends Channel, GuildObject {

	// public PermissionOverwrites permission_overwrites;

	@Override
	default String guild_id() {
		return getData().getString("guild_id");
	}

	default String name() {
		return getData().getString("name");
	}

	default Long position() {
		return getData().getLong("position");
	}

	default CategoryChannel parent() throws Exception {
		final var parent_id = getData().getString("parent_id");
		return (CategoryChannel)client().channels.fetch(parent_id).get();
	}

}
