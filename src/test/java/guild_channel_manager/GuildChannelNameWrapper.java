package guild_channel_manager;

import discord.structures.channels.GuildChannel;

final class GuildChannelNameWrapper {
	final GuildChannel channel;

	GuildChannelNameWrapper(GuildChannel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return channel.name();
	}
}
