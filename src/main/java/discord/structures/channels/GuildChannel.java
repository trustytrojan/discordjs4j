package discord.structures.channels;

import discord.structures.GuildObject;

public interface GuildChannel extends Channel, GuildObject {

	// public PermissionOverwrites permission_overwrites;

	@Override
	default String url() {
		return "https://discord.com/channels/"+guildId()+'/'+id();
	}

	@Override
	default String guildId() {
		return getData().getString("guild_id");
	}

	default Long position() {
		return getData().getLong("position");
	}

	default CategoryChannel parent() {
		final var parent_id = getData().getString("parent_id");
		try {
			return (CategoryChannel)client().channels.fetch(parent_id).get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
