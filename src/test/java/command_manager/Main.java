package command_manager;

import static command_manager.Swing.model;
import static command_manager.Discord.client;

class CommandSetter {
	private static void updateCacheAndTable() {
		final var row = new Object[4];
		client.commands.refresh().join();
		for (final var command : client.commands) {
			row[0] = command.id();
			row[1] = command.name();
			row[2] = command.description();
			row[3] = command.type();
			model.addRow(row);
		}
	}

	public static void main(String[] __) {
		updateCacheAndTable();
	}
}
