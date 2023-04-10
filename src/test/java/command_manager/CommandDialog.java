package command_manager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import discord.structures.ApplicationCommand;
import discord.structures.ApplicationCommandOption;

public class CommandDialog {

	private static final Insets INSETS_5 = new Insets(5, 5, 5, 5);

	static final JLabel typeLabel = new JLabel("Type");
	static final JComboBox<ApplicationCommand.Type> typeInput = new JComboBox<>();
	static {
		typeInput.addItem(ApplicationCommand.Type.CHAT_INPUT);
		typeInput.addItem(ApplicationCommand.Type.MESSAGE);
		typeInput.addItem(ApplicationCommand.Type.USER);
		SwingUtils.onAction(typeInput, (e) -> {
			final var value = (ApplicationCommand.Type) typeInput.getSelectedItem();
			System.out.println(value);
		});
	}

	static final JLabel nameLabel = new JLabel("Name");
	static final JTextField nameInput = new JTextField();

	static final JLabel descLabel = new JLabel("Description");
	static final JTextField descInput = new JTextField();

	static final JLabel optionsLabel = new JLabel("Options");
	static final DefaultTableModel model = new DefaultTableModel();
	static final JTable optionsTable = new JTable(model);
	static {
		model.setColumnIdentifiers(new String[] { "Type", "Name", "Description", "Required?" });
	}

	static void clearInputs() {
		typeInput.setSelectedIndex(0);
		nameInput.setText(null);
		descInput.setText(null);
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

	final JFrame owner;
	final JDialog dialog;

	CommandDialog(JFrame owner) {
		this.owner = owner;
		dialog = new JDialog(owner, "Create/Edit Command");
		dialog.setLayout(new GridBagLayout());

		dialog.add(typeLabel, constraints(0, 0));
		dialog.add(typeInput, constraints(1, 0));
		dialog.add(nameLabel, constraints(0, 1));
		dialog.add(nameInput, constraints(1, 1));
		dialog.add(descLabel, constraints(0, 2));
		dialog.add(descInput, constraints(1, 2));

		var c = new GridBagConstraints();
		c.gridy = 3;
		c.gridwidth = 2;
		dialog.add(optionsLabel, c);

		c = new GridBagConstraints();
		c.gridy = 4;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 1;
		dialog.add(new JScrollPane(optionsTable), c);
	}

	void fillOptions(Iterable<ApplicationCommandOption> options) {
		final var rowCount = model.getRowCount();
		for (int i = 0; i < rowCount; ++i)
			model.removeRow(i);
		for (final var option : options)
			model.addRow(new String[] { option.type.toString(), option.name, option.description, String.valueOf(option.required) });
	}

	void setFields(ApplicationCommand command) {
		typeInput.setSelectedItem(command.type());
		nameInput.setText(command.name());
		descInput.setText(command.description());
		fillOptions(command.options());
	}

	void show() {
		dialog.validate();
		dialog.pack();
		dialog.setVisible(true);
	}

}
