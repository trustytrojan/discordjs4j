package discord.structures.channels;

import discord.structures.Guild;

public interface GuildChannel extends Channel {

	//public PermissionOverwrites permission_overwrites;

	public String guild_id();
	public Guild guild();

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
