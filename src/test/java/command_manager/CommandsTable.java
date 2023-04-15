package command_manager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import discord.structures.ApplicationCommand;
import java_signals.Signal1;

public class CommandsTable extends MyTable {
	public final Signal1<String> editRequested = new Signal1<>();
	public final Signal1<String> deleteRequested = new Signal1<>();

	public CommandsTable() {
		super("ID", "Type", "Name", "Description", "Options");

		final var dropdown = new JPopupMenu();

		dropdown.add("Edit").addActionListener((final var e) -> {
			final var commandId = (String) getValueAt(getSelectedRow(), 0);
			editRequested.emit(commandId);
		});

		dropdown.add("Delete").addActionListener((final var e) -> {
			final var commandId = (String) getValueAt(getSelectedRow(), 0);
			deleteRequested.emit(commandId);
		});

		// show dropdown on mouse release
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					dropdown.show(CommandsTable.this, e.getX(), e.getY());
				}
			}
		});
	}

	public void addRow(ApplicationCommand command) {
		addRow(command.id(), command.type(), command.name(), command.description(), command.options().size() + " options...");
	}
}
