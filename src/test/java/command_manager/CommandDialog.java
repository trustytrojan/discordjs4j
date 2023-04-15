package command_manager;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import discord.structures.ApplicationCommand;
import discord.structures.ApplicationCommandOption;

final class CommandDialog extends JDialog {
	private static final Insets INSETS_5 = new Insets(5, 5, 5, 5);

	private static GridBagConstraints formConstraints(int gridx, int gridy) {
		final var c = new GridBagConstraints();
		c.insets = INSETS_5;
		c.gridx = gridx;
		c.gridy = gridy;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		return c;
	}

	private final CommandTypeDropdown typeInput = new CommandTypeDropdown();
	private final JTextField nameInput = new JTextField();
	private final JTextField descInput = new JTextField();
	private final CommandOptionsTable optionsTable = new CommandOptionsTable();
	private boolean okPressed;

	CommandDialog() {
		// Don't submit any data when the window is closed
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				okPressed = false;
			}
		});

		setModalityType(ModalityType.APPLICATION_MODAL);

		add(panelInit());
		validate();
		pack();
	}

	private JPanel panelInit() {
		final var panel = new JPanel(new GridBagLayout());

		panel.add(new JLabel("Type"), formConstraints(0, 0));
		panel.add(typeInput, formConstraints(1, 0));
		panel.add(new JLabel("Name"), formConstraints(0, 1));
		panel.add(nameInput, formConstraints(1, 1));
		panel.add(new JLabel("Description"), formConstraints(0, 2));
		panel.add(descInput, formConstraints(1, 2));

		var c = new GridBagConstraints();
		c.insets = INSETS_5;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.SOUTH;
		panel.add(new JLabel("Options"), c);

		c = new GridBagConstraints();
		c.insets = INSETS_5;
		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.EAST;
		panel.add(createAddOptionButton(), c);

		c = new GridBagConstraints();
		c.gridy = 4;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new JScrollPane(optionsTable), c);

		c = new GridBagConstraints();
		c.insets = INSETS_5;
		c.gridy = 5;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.EAST;
		panel.add(createExitButtonsPanel(), c);

		return panel;
	}

	private JButton createAddOptionButton() {
		final var addOptionButton = new JButton("Add Option");
		addOptionButton.addActionListener((final var e) -> optionsTable.addRow());
		return addOptionButton;
	}

	private JPanel createExitButtonsPanel() {
		final var createButton = new JButton("Create");
		createButton.addActionListener((final var e) -> {
			okPressed = true;
			setVisible(false);
		});

		final var cancelButton = new JButton("Cancel");
		cancelButton.addActionListener((final var e) -> {
			okPressed = false;
			setVisible(false);
		});

		final var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	private void clearInputs() {
		typeInput.setSelectedItem(null);
		nameInput.setText(null);
		descInput.setText(null);
		optionsTable.clear();
	}

	void fillInputs(ApplicationCommand command) {
		typeInput.setSelectedItem(command.type());
		nameInput.setText(command.name());
		descInput.setText(command.description());
		for (final var option : command.options()) {
			optionsTable.addRow(option.type, option.name, option.description, option.required);
		}
	}

	ApplicationCommand.Payload showAndWait() {
		clearInputs();
		setVisible(true);

		if (!okPressed) {
			return null;
		}

		final var newCommand = new ApplicationCommand.Payload((ApplicationCommand.Type) typeInput.getSelectedItem(),
				nameInput.getText(), descInput.getText());

		for (final var row : optionsTable.rows()) {
			if (row == null)
				continue;
			newCommand.addOption((ApplicationCommandOption.Type) row.get(0), (String) row.get(1), (String) row.get(2),
					(boolean) row.get(3));
		}

		return newCommand;
	}
}