package guild_channel_manager;

import discord.structures.channels.GuildChannel;

public class GuildChannelEditRequest {
	final String commandId;
	final int rowInTable;
	GuildChannel currentChannel;
	GuildChannel.Payload payload;

	GuildChannelEditRequest(final String commandId, final int rowInTable) {
		this.commandId = commandId;
		this.rowInTable = rowInTable;
	}
}
