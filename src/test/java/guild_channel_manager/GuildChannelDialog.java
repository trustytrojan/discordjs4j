package guild_channel_manager;

import java.awt.GridBagConstraints;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import discord.structures.channels.Channel;
import swing_extensions.BottomButtonsPanel;
import swing_extensions.FormPanel;
import swing_extensions.MyDialog;
import swing_extensions.SwingUtils;

final class GuildChannelDialog extends MyDialog {
	private final JComboBox<Channel.Type> typeInput = new JComboBox<>(Channel.Type.values());
	private final JButton cancelButton = SwingUtils.button("Cancel", this::dispose);
	private final JButton nextButton = SwingUtils.button("Next", this::nextClicked);

	public Consumer<Channel.Type> createRequested;

	GuildChannelDialog(Window owner) {
		super(owner, "Create Channel");
		typeInput.addActionListener(__ -> nextButton.setEnabled(typeInput.getSelectedIndex() != -1));
		setContentPane(createMainPanel());
		validate();
		pack();
	}

	private JPanel createMainPanel() {
		final var panel = new FormPanel();
		GridBagConstraints c;

		c = FormPanel.constraintsInsets5();
		c.anchor = GridBagConstraints.WEST;
		panel.add(new JLabel("Channel Type"), c);

		c = FormPanel.constraintsInsets5();
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(typeInput, c);

		c = FormPanel.constraintsInsets5();
		c.gridy = 2;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(new BottomButtonsPanel(cancelButton, nextButton), c);

		return panel;
	}

	public void showCreate() {
		typeInput.setSelectedIndex(-1);
		setVisible(true);
	}

	private void nextClicked() {
		createRequested.accept((Channel.Type) typeInput.getSelectedItem());
		dispose();
	}
}
