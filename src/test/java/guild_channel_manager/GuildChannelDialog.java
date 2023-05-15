package guild_channel_manager;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import discord.structures.channels.Channel;
import swing_extensions.GridBagPanel;
import swing_extensions.MyDialog;
import swing_extensions.SwingUtils;

final class GuildChannelDialog extends MyDialog {
	private final JComboBox<Channel.Type> typeInput = new JComboBox<>(Channel.Type.values());
	private final JButton cancelButton = SwingUtils.button("Cancel", this::dispose);
	private final JButton nextButton = SwingUtils.button("Next", this::nextClicked);

	public Consumer<Channel.Type> typeSelected;

	GuildChannelDialog(Window owner) {
		super(owner, "Create Channel");
		typeInput.addActionListener(e -> nextButton.setEnabled(typeInput.getSelectedIndex() != -1));
		setContentPane(createMainPanel());
		validate();
		pack();
	}

	private JPanel createMainPanel() {
		final var panel = new GridBagPanel();
		var c = GridBagPanel.constraintsInsets5();

		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		panel.add(new JLabel("Choose the channel type:"), c);

		c.gridy = 1;
		panel.add(typeInput, c);

		c.gridy = 2;
		c.anchor = GridBagConstraints.EAST;
		final var buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(cancelButton);
		buttons.add(nextButton);
		panel.add(buttons, c);

		return panel;
	}

	public void display() {
		typeInput.setSelectedIndex(-1);
		setVisible(true);
	}

	private void nextClicked() {
		typeSelected.accept((Channel.Type) typeInput.getSelectedItem());
		dispose();
	}
}
