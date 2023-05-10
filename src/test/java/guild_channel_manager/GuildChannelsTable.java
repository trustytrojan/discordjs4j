package guild_channel_manager;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import swing_extensions.MyTable;

final class GuildChannelsTable extends MyTable {
	Consumer<GuildChannelEditRequest> editClicked;
	BiConsumer<Integer, String> deleteClicked;

	GuildChannelsTable() {
		super("ID", "Type", "Name");
	}
}
