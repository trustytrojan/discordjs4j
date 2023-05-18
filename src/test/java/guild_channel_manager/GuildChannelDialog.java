package guild_channel_manager;

import java.awt.GridBagConstraints;
import java.awt.Window;
import java.util.HashMap;
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
	private static final HashMap<String, Channel.Type> GUILD_CHANNEL_TYPES = new HashMap<>();

	static {
		GUILD_CHANNEL_TYPES.put("Text", Channel.Type.GUILD_TEXT);
		GUILD_CHANNEL_TYPES.put("Category", Channel.Type.GUILD_CATEGORY);
	}

	private final JComboBox<String> typeInput = new JComboBox<>();
	private final JButton cancelButton = SwingUtils.button("Cancel", this::dispose);
	private final JButton nextButton = SwingUtils.button("Next", this::nextClicked);

	public Consumer<Channel.Type> createRequested;

	GuildChannelDialog(Window owner) {
		super(owner, "Create Channel");

		GUILD_CHANNEL_TYPES.keySet().forEach(typeInput::addItem);
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
		createRequested.accept(GUILD_CHANNEL_TYPES.get(typeInput.getSelectedItem()));
		dispose();
	}
}
