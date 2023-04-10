package command_manager;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import discord.structures.ApplicationCommand;

import static command_manager.Discord.client;

class MainWindow {
	static final JFrame frame = new JFrame("DCM");

	static final DefaultTableModel tableModel = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int _1, int _2) {
			return false;
		}
	};
	private static final JTable table = new JTable(tableModel);
	private static final JScrollPane scrollPane = new JScrollPane(table);

	private static int selectedRow = -1;
	private static ApplicationCommand selectedCommand;

	private static final JPopupMenu editDeletePopupMenu = new JPopupMenu();
	static {
		final var edit = editDeletePopupMenu.add("Edit");
		SwingUtils.onAction(edit, (e) -> {
			CommandDialog.setFields(selectedCommand);
			CommandDialog.show();
		});

		final var delete = editDeletePopupMenu.add("Delete");
		SwingUtils.onAction(delete, (e) -> {
			final var option = JOptionPane.showConfirmDialog(delete, "Delete command?");
			if (option == JOptionPane.OK_OPTION && selectedRow >= 0)
				tableModel.removeRow(selectedRow);
		});
	}

	// Menu bar setup
	private static final JMenuBar menuBar = new JMenuBar();
	private static final JMenu commandMenu = new JMenu("Command");

	// add entries for command menu
	static {
		final var createCommand = commandMenu.add("Create...");
		SwingUtils.onAction(createCommand, (e) -> {
			CommandDialog.clearInputs();
			CommandDialog.show();
		});
		menuBar.add(commandMenu);
	}

	static {
		tableModel.setColumnIdentifiers(new String[] { "ID", "Name", "Description", "Type" });

		/**
		 * When a table cell is right clicked, get the ID of the corresponding command
		 * by looking at column 0.
		 */
		SwingUtils.onRightClick(table, (e) -> {
			final var point = e.getPoint();
			selectedRow = table.rowAtPoint(point);
			final var data = (String) table.getValueAt(selectedRow, 0);
			selectedCommand = client.commands.fetch(data);
			editDeletePopupMenu.show(table, e.getX(), e.getY());
			System.out.println(data);
		});

		frame.setJMenuBar(menuBar);
		frame.setPreferredSize(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(scrollPane);
		frame.validate();
		frame.pack();
		frame.setVisible(true);
	}

	static void show() {
		frame.setVisible(true);
	}
}
