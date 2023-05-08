package command_manager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import discord.structures.ApplicationCommand;
import signals.Signal1;
import signals.Signal2;

final class CommandsTable extends MyTable {
	final Signal1<CommandEditRequest> editClicked = new Signal1<>();
	final Signal2<Integer, String> deleteClicked = new Signal2<>();

	CommandsTable() {
		super("ID", "Type", "Name", "Description", "Options");

		final var dropdown = new JPopupMenu();

		dropdown.add("Edit").addActionListener((final var e) -> {
			final var row = getSelectedRow();
			final var commandId = (String) getValueAt(getSelectedRow(), 0);
			// begin an edit request: pack id and row then send to manager
			editClicked.emit(new CommandEditRequest(commandId, row));
		});

		dropdown.add("Delete").addActionListener((final var e) -> {
			final var row = getSelectedRow();
			final var commandId = (String) getValueAt(row, 0);
			deleteClicked.emit(row, commandId);
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

	void addRow(final ApplicationCommand command) {
		addRow(command.id(), command.type(), command.name(), command.description(), command.options().size() + " options...");
	}

	void setRow(final int row, final ApplicationCommand command) {
		setRow(row, command.id(), command.type(), command.name(), command.description(), command.options().size() + " options...");
	}
}
