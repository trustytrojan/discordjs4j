package guild_channel_manager;

import discord.structures.channels.GuildChannel;

public class GuildChannelEditRequest {
	final int rowInTable;
	final GuildChannel channel;
	GuildChannel.Payload payload;

	GuildChannelEditRequest(GuildChannel channel, int rowInTable) {
		this.channel = channel;
		this.rowInTable = rowInTable;
	}
}
