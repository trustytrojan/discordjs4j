package guild_channel_manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import discord.managers.guild.GuildChannelManager;
import discord.structures.channels.GuildChannel;
import discord.structures.channels.TextChannel;
import discord.structures.channels.Channel;
import swing_extensions.GridBagPanel;
import swing_extensions.MyDialog;

public class TextChannelDialog extends MyDialog {
	private static class GuildChannelWrapper {
		final GuildChannel channel;
		GuildChannelWrapper(GuildChannel channel) {
			this.channel = channel;
		}
		@Override
		public String toString() {
			return channel.name();
		}
	}

	private final JTextField nameInput = new JTextField();
	private final JComboBox<GuildChannelWrapper> parentInput = new JComboBox<>();
	private final JTextArea topicInput = new JTextArea();
	private final JSlider slowmodeInput = new JSlider(0, 21_600);
	private final JCheckBox nsfwCheckBox = new JCheckBox("Mark as Age-Restricted");
	private final JCheckBox announcementCheckBox = new JCheckBox("Convert to Announcement Channel");

	private final GuildChannelManager dataManager;
	private GuildChannelEditRequest editRequest;

	public Consumer<GuildChannel.Payload> createRequested;
	public Consumer<GuildChannelEditRequest> editRequested;

	TextChannelDialog(Window owner, GuildChannelManager dataManager) {
		super(owner, "Create Text Channel");
		this.dataManager = dataManager;

		topicInput.setPreferredSize(new Dimension(200, 100));
		topicInput.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		setContentPane(createMainPanel());
		validate();
		pack();
	}

	public void showCreate() {
		editRequest = null;
		clearInputs();
		announcementCheckBox.setEnabled(false);
		parentDropdownRefresh();
		setTitle("Create Text Channel");
		setVisible(true);
	}

	public void showEdit(GuildChannelEditRequest editRequest) {
		this.editRequest = editRequest;
		fillInputs((TextChannel) editRequest.channel);
		announcementCheckBox.setEnabled(true);
		parentDropdownRefresh();
		setTitle("Edit Text Channel - " + editRequest.channel.name());
		setVisible(true);
	}

	private void clearInputs() {
		nameInput.setText(null);
		topicInput.setText(null);
		slowmodeInput.setValue(0);
		nsfwCheckBox.setSelected(false);
	}

	private void fillInputs(TextChannel channel) {
		Objects.requireNonNull(channel);
		nameInput.setText(channel.name());
		topicInput.setText(channel.topic());
		slowmodeInput.setValue(channel.slowmodeDuration().intValue());
		nsfwCheckBox.setSelected(channel.nsfw());
	}

	private JPanel createMainPanel() {
		final var panel = new GridBagPanel();
		GridBagConstraints c;

		panel.addFormRow(0, "Name", nameInput);
		panel.addFormRow(1, "Category", parentInput);
		panel.addFormRow(2, "Topic", topicInput);
		panel.addFormRow(3, "Slowmode Duration (seconds)", slowmodeInput);

		c = GridBagPanel.constraintsInsets5();
		c.gridy = 4;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		panel.add(nsfwCheckBox, c);

		c.gridy = 5;
		panel.add(announcementCheckBox, c);

		c = GridBagPanel.constraintsInsets5();
		c.gridy = 6;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.EAST;
		panel.add(createExitButtonsPanel(), c);

		return panel;
	}

	private void parentDropdownRefresh() {
		dataManager.refreshCache().thenRun(() -> {
			dataManager.cache.values().stream()
				.filter(c -> (c.type() == Channel.Type.GUILD_CATEGORY))
				.sorted((a, b) -> a.position().compareTo(b.position()))
				.forEach(c -> parentInput.addItem(new GuildChannelWrapper(c)));
		});
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

	private void onSendPressed() {
		final var payload = new TextChannel.Payload(nameInput.getText());

		if (payload.name == null || payload.name.isBlank()) {
			JOptionPane.showMessageDialog(this, "Name is blank!");
			return;
		}

		{
			final var selectedParent = (GuildChannelWrapper) parentInput.getSelectedItem();
			if (selectedParent != null) {
				payload.parentId = selectedParent.channel.id();
			}
		}

		{
			final var slowmodeDuration = (short) slowmodeInput.getValue();
			if (slowmodeDuration > 0) {
				payload.rateLimitPerUser = slowmodeDuration;
			}
		}

		payload.topic = topicInput.getText();

		if (announcementCheckBox.isSelected()) {
			payload.type = Channel.Type.GUILD_ANNOUNCEMENT;
		}

		payload.nsfw = nsfwCheckBox.isSelected();

		if (editRequest != null) {
			editRequest.payload = payload;
			editRequested.accept(editRequest);
		} else {
			createRequested.accept(payload);
		}

		dispose();
	}
}
