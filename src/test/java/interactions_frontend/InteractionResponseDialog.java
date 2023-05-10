package interactions_frontend;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import discord.structures.interactions.Interaction;
import signals.Signal1;
import swing_extensions.MyDialog;

final class InteractionResponseDialog extends MyDialog {
	private static final Insets INSETS_5 = new Insets(5, 5, 5, 5);

	private static GridBagConstraints constraintsInsets5() {
		final var c = new GridBagConstraints();
		c.insets = INSETS_5;
		return c;
	}

	private final JCheckBox ephemeralCheckBox = new JCheckBox();
	private final JTextArea contentTextArea = new JTextArea();

	public final Signal1<Interaction.Response> responseCreated = new Signal1<>();

	InteractionResponseDialog(final Window owner) {
		super(owner, "Create Interaction Response");
		setContentPane(panelInit());
		validate();
		pack();
	}

	private void clearInputs() {
		ephemeralCheckBox.setSelected(false);
		contentTextArea.setText(null);
	}

	void showCreate() {
		clearInputs();
		setVisible(true);
	}

	private JPanel panelInit() {
		final var panel = new JPanel(new GridBagLayout());

		var c = constraintsInsets5();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		panel.add(new JLabel("Message Content"), c);

		c.gridx = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(contentTextArea, c);

		c = constraintsInsets5();
		c.gridy = 1;
		panel.add(ephemeralCheckBox, c);

		c = constraintsInsets5();
		c.gridy = 2;
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
		cancelButton.addActionListener(e -> dispose());

		final var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	private void onSendPressed(final ActionEvent e) {
		final var resp = new Interaction.Response(ephemeralCheckBox.isSelected());

		resp.content = contentTextArea.getText();

		if (resp.content == null || resp.content.isBlank()) {
			JOptionPane.showMessageDialog(this, "Content is empty!", "Required", ABORT);
			return;
		}

		responseCreated.emit(resp);

		dispose();
	}
}
