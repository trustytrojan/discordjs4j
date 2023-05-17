package guild_channel_manager;

import discord.structures.channels.GuildChannel;
import swing_extensions.MyTable;

final class GuildChannelsTable extends MyTable {
	private static Object[] toDataRow(GuildChannel channel) {
		final var parent = channel.parent().join();
		return new Object[] { channel.id(), channel.type(), (parent == null) ? null : parent.name(), channel.name() };
	}

	GuildChannelsTable() {
		super("ID", "Type", "Parent", "Name");
	}

	void addRow(GuildChannel channel) {
		addRow(toDataRow(channel));
	}

	void setRow(int row, GuildChannel channel) {
		setRow(row, toDataRow(channel));
	}
}
