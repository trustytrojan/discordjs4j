package command_manager;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.util.LinkedList;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import discord.structures.ApplicationCommand;
import discord.structures.ApplicationCommandOption;
import swing_extensions.GridBagPanel;
import swing_extensions.MyDialog;
import swing_extensions.SwingUtils;

final class CommandDialog extends MyDialog {
	private final JComboBox<ApplicationCommand.Type> typeInput = new JComboBox<>(ApplicationCommand.Type.values());
	private final JTextField nameInput = new JTextField();
	private final JTextField descInput = new JTextField();
	private final CommandOptionsTable optionsTable = new CommandOptionsTable();
	private final JButton addOptionButton = new JButton("Add Option");

	Consumer<ApplicationCommand.Payload> createRequested;
	Consumer<CommandEditRequest> editRequested;

	private CommandEditRequest editRequest;

	CommandDialog(Window owner) {
		super(owner, "Create/Edit Command");
		typeDropdownInit();
		setContentPane(createMainPanel());
		validate();
		pack();
	}

	private void clearInputs() {
		typeInput.setSelectedItem(0);
		nameInput.setText(null);
		descInput.setText(null);
		optionsTable.clear();
	}

	private void fillInputs(ApplicationCommand command) {
		typeInput.setSelectedItem(command.type());
		nameInput.setText(command.name());
		descInput.setText(command.description());
		optionsTable.clearAndFill(command.options());
	}

	// Call when user wants to CREATE a command
	void showCreate() {
		editRequest = null;
		clearInputs();
		setTitle("Create Command");
		setVisible(true);
	}

	// Call when user wants to EDIT a command
	void showEdit(CommandEditRequest editRequest) {
		this.editRequest = editRequest;
		fillInputs(editRequest.currentCommand);
		setTitle("Edit Command");
		setVisible(true);
	}

	private void typeDropdownInit() {
		typeInput.setSelectedItem(0);
		typeInput.addActionListener(e -> {
			final var selectedItem = typeInput.getSelectedItem();
			if (selectedItem == null)
				return;
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

	private JPanel createMainPanel() {
		final var panel = new GridBagPanel();

		panel.addFormRow(0, "Type", typeInput);
		panel.addFormRow(1, "Name", nameInput);
		panel.addFormRow(2, "Description", descInput);

		var c = GridBagPanel.constraintsInsets5();
		c.gridy = 3;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.SOUTH;
		panel.add(new JLabel("Options"), c);

		c = GridBagPanel.constraintsInsets5();
		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.EAST;
		panel.add(SwingUtils.button("Add Option", optionsTable::addRow), c);

		c = new GridBagConstraints();
		c.gridy = 4;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new JScrollPane(optionsTable), c);

		c = GridBagPanel.constraintsInsets5();
		c.gridy = 5;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.EAST;
		panel.add(createExitButtonsPanel(), c);

		return panel;
	}

	private JPanel createExitButtonsPanel() {
		final var createButton = new JButton("Send to Discord");
		createButton.addActionListener(e -> onSendPressed());

		final var cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> dispose());

		final var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

		// When user presses "Send to Discord"
		private void onSendPressed() {
			final var payload = new ApplicationCommand.Payload();
	
			// REQUIRED field
			payload.name = nameInput.getText();
	
			if (payload.name == null || payload.name.isBlank()) {
				JOptionPane.showMessageDialog(this, "Name is blank!", "Empty Input", JOptionPane.ERROR_MESSAGE);
				return;
			}
	
			final var selectedType = (ApplicationCommand.Type) typeInput.getSelectedItem();
			switch (selectedType) {
				case CHAT_INPUT -> {
					// REQUIRED field for CHAT_INPUT only!
					payload.description = descInput.getText();
	
					if (payload.description == null || payload.description.isBlank()) {
						JOptionPane.showMessageDialog(this, "Description is blank!", "Empty Input",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
	
					payload.options = new LinkedList<>();
	
					// fill payload with options from table
					for (final var row : optionsTable.rows()) {
						if (row == null)
							continue;
						final var option = new ApplicationCommandOption.NonSubcommandPayload();
						option.type = (ApplicationCommandOption.Type) row.get(0);
						option.name = (String) row.get(1);
						option.description = (String) row.get(2);
						option.required = (row.get(3) == null) ? false : true;
						payload.options.add(option);
					}
				}
	
				default -> {
					// other types should *not* have a description
					payload.type = selectedType;
				}
			}
	
			// Very important: check if this is an edit or a create!!!
			if (editRequest != null) {
				// pack the payload into the request and send it back to the manager
				editRequest.payload = payload;
				editRequested.accept(editRequest);
			} else {
				createRequested.accept(payload);
			}
	
			dispose();
		}
}