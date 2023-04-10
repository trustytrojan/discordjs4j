package command_manager;

import static command_manager.Swing.TABLE_MODEL;
import static command_manager.Discord.CLIENT;

class CommandSetter {
	private static void updateCacheAndTable() {
		final String[] row = new String[3];
		CLIENT.commands.refresh().join();
		for (final var command : CLIENT.commands) {
			row[0] = command.id();
			row[1] = command.name();
			row[2] = command.description();
			TABLE_MODEL.addRow(row);
		}
	}

	public static void main(String[] __) {
		updateCacheAndTable();
	}
}
