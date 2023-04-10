package command_manager;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import discord.structures.ApplicationCommand;

import static command_manager.Discord.client;

class Swing {


	static final JFrame frame = new JFrame("DCM");
	static final CommandDialog commandDialog = new CommandDialog(frame);

	static final DefaultTableModel model = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int _1, int _2) {
			return false;
		}
	};
	static final JTable table = new JTable(model);
	static final JScrollPane scrollPane = new JScrollPane(table);

	static int selectedRow = -1;
	static ApplicationCommand selectedCommand;

	static final JPopupMenu editDeletePopupMenu = new JPopupMenu();
	static {
		final var edit = editDeletePopupMenu.add("Edit");
		SwingUtils.onAction(edit, (e) -> {
			commandDialog.setFields(selectedCommand);
			commandDialog.show();
		});

		final var delete = editDeletePopupMenu.add("Delete");
		SwingUtils.onAction(delete, (e) -> {
			final var option = JOptionPane.showConfirmDialog(delete, "Delete command?");
			if (option == JOptionPane.OK_OPTION && selectedRow >= 0)
				model.removeRow(selectedRow);
		});
	}

	static final JLabel commandTypeLabel = new JLabel("Type:");
	static final JComboBox<ApplicationCommand.Type> commandTypeDropdown = new JComboBox<>();
	static {
		commandTypeDropdown.addItem(ApplicationCommand.Type.CHAT_INPUT);
		commandTypeDropdown.addItem(ApplicationCommand.Type.MESSAGE);
		commandTypeDropdown.addItem(ApplicationCommand.Type.USER);
		SwingUtils.onAction(commandTypeDropdown, (e) -> {
			final var value = (ApplicationCommand.Type) commandTypeDropdown.getSelectedItem();
			System.out.println(value);
		});
	}

	static final JPopupMenu createPopupMenu = new JPopupMenu();
	static {
		final var create = createPopupMenu.add("Create");
		SwingUtils.onAction(create, (e) -> {
			commandDialog.show();
		});
	}

	static {
		model.setColumnIdentifiers(new String[] { "ID", "Name", "Description", "Type" });

		// when a command is right clicked
		SwingUtils.onRightClick(table, (e) -> {
			final var point = e.getPoint();
			selectedRow = table.rowAtPoint(point);
			final var data = (String) table.getValueAt(selectedRow, 0);
			selectedCommand = client.commands.fetch(data);
			editDeletePopupMenu.show(table, e.getX(), e.getY());
			System.out.println(data);
		});

		// when right clicked below table
		SwingUtils.onRightClick(scrollPane, (e) -> {
			createPopupMenu.show(scrollPane, e.getX(), e.getY());
		});

		frame.setPreferredSize(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(scrollPane);
		frame.validate();
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] __) {
	}

}
