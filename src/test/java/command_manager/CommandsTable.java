package command_manager;

import discord.structures.ApplicationCommand;

public class CommandsTable extends MyTable {
	public CommandsTable() {
		super("ID", "Type", "Name", "Description", "Options");
	}

	public void addRow(ApplicationCommand command) {
		addRow(command.id(), command.type(), command.name(), command.description(), command.options().size() + " options...");
	}
}
