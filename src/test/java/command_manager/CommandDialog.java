package command_manager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import discord.structures.ApplicationCommand;
import discord.structures.ApplicationCommandOption;

final class CommandDialog {
	private static final Insets INSETS_5 = new Insets(5, 5, 5, 5);

	private static final JLabel typeLabel = new JLabel("Type");
	private static final JComboBox<ApplicationCommand.Type> typeInput = new JComboBox<>();

	// type dropdown menu init
	static {
		typeInput.addItem(ApplicationCommand.Type.CHAT_INPUT);
		typeInput.addItem(ApplicationCommand.Type.MESSAGE);
		typeInput.addItem(ApplicationCommand.Type.USER);
		SwingUtils.onAction(typeInput, (e) -> {
			final var value = (ApplicationCommand.Type) typeInput.getSelectedItem();
			System.out.println(value);
		});
	}

	private static final JLabel nameLabel = new JLabel("Name");
	private static final JTextField nameInput = new JTextField();

	private static final JLabel descLabel = new JLabel("Description");
	private static final JTextField descInput = new JTextField();

	private static final JLabel optionsLabel = new JLabel("Options");
	private static final DefaultTableModel model = new DefaultTableModel();
	private static final JTable optionsTable = new JTable(model);

	// options table init
	static {
		model.setColumnIdentifiers(new String[] { "Type", "Name", "Description", "Required?" });
	}

	private static void clearOptionsTable() {
		final var rowCount = model.getRowCount();
		for (int i = 0; i < rowCount; ++i)
			try {
				model.removeRow(i); 
			} catch (ArrayIndexOutOfBoundsException e) {
			}
	}

	static void clearInputs() {
		typeInput.setSelectedIndex(0);
		nameInput.setText(null);
		descInput.setText(null);
		clearOptionsTable();
	}

	private static final JButton addOptionButton = new JButton("Add Option");

	// button listener init
	static {
		SwingUtils.onAction(addOptionButton, (e) -> {
			model.addRow(new Object[0]);
		});
	}

	static GridBagConstraints constraints(int gridx, int gridy) {
		final var c = new GridBagConstraints();
		c.insets = INSETS_5;
		c.gridx = gridx;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		return c;
	}

	private static final JDialog dialog = new JDialog(MainWindow.frame, "Create/Edit Command");

	// dialog init
	static {
		dialog.setLayout(new GridBagLayout());

		dialog.add(typeLabel, constraints(0, 0));
		dialog.add(typeInput, constraints(1, 0));
		dialog.add(nameLabel, constraints(0, 1));
		dialog.add(nameInput, constraints(1, 1));
		dialog.add(descLabel, constraints(0, 2));
		dialog.add(descInput, constraints(1, 2));

		var c = new GridBagConstraints();
		c.gridy = 3;
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		dialog.add(optionsLabel, c);

		c = new GridBagConstraints();
		c.gridy = 3;
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		dialog.add(addOptionButton, c);

		c = new GridBagConstraints();
		c.gridy = 4;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1;
		dialog.add(new JScrollPane(optionsTable), c);
	}

	static void clearAndFillOptions(Iterable<ApplicationCommandOption> options) {
		clearOptionsTable();
		final var row = new Object[4];
		for (final var option : options) {
			row[0] = option.type.toString();
			row[1] = option.name;
			row[2] = option.description;
			row[3] = String.valueOf(option.required);
		}
	}

	static void setFields(ApplicationCommand command) {
		typeInput.setSelectedItem(command.type());
		nameInput.setText(command.name());
		descInput.setText(command.description());
		clearAndFillOptions(command.options());
	}

	static void show() {
		dialog.validate();
		dialog.pack();
		dialog.setVisible(true);
	}

	private CommandDialog() {}
}
