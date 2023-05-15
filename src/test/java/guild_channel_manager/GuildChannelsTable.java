package guild_channel_manager;

import discord.structures.channels.GuildChannel;
import swing_extensions.MyTable;

final class GuildChannelsTable extends MyTable {
	GuildChannelsTable() {
		super("ID", "Type", "Parent", "Name");
	}

	void addRow(GuildChannel channel) {
		channel.fetchParent().thenAccept(parent -> 
			addRow(channel.id(), channel.type(), (parent == null) ? null : parent.name(), channel.name())
		);
	}

	void setRow(int row, GuildChannel channel) {
		channel.fetchParent().thenAccept(parent -> 
			setRow(row, channel.id(), channel.type(), (parent == null) ? null : parent.name(), channel.name())
		);
	}
}
