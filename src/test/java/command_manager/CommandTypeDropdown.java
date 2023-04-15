package command_manager;

import javax.swing.JComboBox;

import discord.structures.ApplicationCommand;

public class CommandTypeDropdown extends JComboBox<ApplicationCommand.Type> {
	CommandTypeDropdown() {
		for (final var type : ApplicationCommand.Type.values()) {
			addItem(type);
		}
	}
}
