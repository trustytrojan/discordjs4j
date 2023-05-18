package guild_channel_manager;

import discord.structures.channels.GuildChannel;

class GuildChannelNameWrapper {
	public static final GuildChannelNameWrapper NO_PARENT_SELECTION =
			new GuildChannelNameWrapper(null) {
				@Override
				public String toString() {
					return "(uncategorized)";
				}
			};

	final GuildChannel channel;

	GuildChannelNameWrapper(GuildChannel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return channel.name();
	}
}
