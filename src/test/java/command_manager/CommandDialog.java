package command_manager;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import discord.structures.ApplicationCommand;
import discord.structures.ApplicationCommandOption;
import java_signals.Signal1;

final class CommandDialog extends MyDialog {
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

	private final JComboBox<ApplicationCommand.Type> typeInput = new JComboBox<>();
	private final JTextField nameInput = new JTextField();
	private final JTextField descInput = new JTextField();
	private final CommandOptionsTable optionsTable = new CommandOptionsTable();
	private final JButton addOptionButton = new JButton("Add Option");

	public final Signal1<ApplicationCommand.Payload> createRequested = new Signal1<>();
	public final Signal1<CommandEditRequest> editRequested = new Signal1<>();

	private CommandEditRequest editRequest;

	CommandDialog(final Window owner) {
		super(owner, "Create/Edit Command");

		typeDropdownInit();
		addOptionButton.addActionListener((final var e) -> optionsTable.addRow());

		add(panelInit());
		validate();
		pack();
	}

	void clearInputs() {
		typeInput.setSelectedItem(0);
		nameInput.setText(null);
		descInput.setText(null);
		optionsTable.clear();
	}

	private void fillInputs(final ApplicationCommand command) {
		typeInput.setSelectedItem(command.type());
		nameInput.setText(command.name());
		descInput.setText(command.description());
		for (final var option : command.options()) {
			optionsTable.addRow(option.type, option.name, option.description, option.required);
		}
	}

	void showCreate() {
		editRequest = null;
		setTitle("Create Command");
		setVisible(true);
	}

	void showEdit(final CommandEditRequest editRequest) {
		this.editRequest = editRequest;
		fillInputs(editRequest.currentCommand);
		setTitle("Edit Command");
		setVisible(true);
	}

	private void onSendPressed(final ActionEvent e) {
		final var payload = new ApplicationCommand.Payload();

		// REQUIRED field
		payload.name = nameInput.getText();

		if (payload.name == null || payload.name.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Name is empty!");
			return;
		}

		final var selectedType = (ApplicationCommand.Type) typeInput.getSelectedItem();
		switch (selectedType) {
			case CHAT_INPUT -> {
				// REQUIRED field for CHAT_INPUT only!
				payload.description = descInput.getText();

				if (payload.description == null || payload.description.isEmpty()) {
					JOptionPane.showMessageDialog(this, "Description is empty!");
					return;
				}

				// fill payload with options from table
				for (final var row : optionsTable.rows()) {
					if (row == null) continue;
					final var type = (ApplicationCommandOption.Type) row.get(0);
					final var name = (String) row.get(1);
					final var description = (String) row.get(2);
					final var required = (Boolean) row.get(3);
					payload.addOption(type, name, description, (required == null) ? false : true);
				}
			}

			default -> {
				// other types *should not have* a description
				payload.type = selectedType;
			}
		}

		if (editRequest != null) {
			// pack the payload into the request and send it back to the manager
			editRequest.payload = payload;
			editRequested.emit(editRequest);
		} else {
			createRequested.emit(payload);
		}

		dispose();
	}

	private void typeDropdownInit() {
		for (final var type : ApplicationCommand.Type.values()) {
			typeInput.addItem(type);
		}

		typeInput.setSelectedItem(0);

		typeInput.addActionListener((final var e) -> {
			final var selectedItem = typeInput.getSelectedItem();
			if (selectedItem == null) return;
			switch ((ApplicationCommand.Type) selectedItem) {
				case CHAT_INPUT -> {
					descInput.setEnabled(true);
					addOptionButton.setEnabled(true);
				}
				default -> {
					descInput.setText(null);
					descInput.setEnabled(false);
					addOptionButton.setEnabled(false);
					optionsTable.clear();
				}
			}
		});
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
		panel.add(addOptionButton, c);

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

	private JPanel createExitButtonsPanel() {
		final var createButton = new JButton("Send to Discord");
		createButton.addActionListener(this::onSendPressed);

		final var cancelButton = new JButton("Cancel");
		cancelButton.addActionListener((final var e) -> dispose());

		final var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}
}