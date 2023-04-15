package command_manager;

import javax.swing.JComboBox;

import discord.structures.ApplicationCommandOption;

public class CommandOptionTypeDropdown extends JComboBox<ApplicationCommandOption.Type> {
	CommandOptionTypeDropdown() {
		for (final var type : ApplicationCommandOption.Type.values()) {
			addItem(type);
		}
	}
}
