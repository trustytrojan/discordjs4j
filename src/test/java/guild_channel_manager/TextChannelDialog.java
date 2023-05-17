package guild_channel_manager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import discord.managers.guild.GuildChannelManager;
import discord.structures.channels.GuildChannel;
import discord.structures.channels.TextChannel;
import discord.structures.channels.Channel;
import swing_extensions.FormPanel;
import swing_extensions.MyDialog;
import swing_extensions.SwingUtils;

public class TextChannelDialog extends MyDialog {
	private final JTextField nameInput = new JTextField();
	private final JComboBox<GuildChannelNameWrapper> parentInput = new JComboBox<>();
	private final JTextArea topicInput = new JTextArea();
	private final JSlider slowmodeInput = new JSlider(0, 21_600);
	private final JCheckBox nsfwCheckBox = new JCheckBox("Mark as Age-Restricted");
	private final JCheckBox announcementCheckBox = new JCheckBox("Convert to Announcement Channel");

	private final GuildChannelManager dataManager;
	private GuildChannelEditRequest editRequest;

	public Consumer<GuildChannel.Payload> createRequested;
	public Consumer<GuildChannelEditRequest> editRequested;

	TextChannelDialog(Window owner, GuildChannelManager dataManager) {
		super(owner);
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

	private FormPanel createMainPanel() {
		final var panel = new FormPanel();

		panel.addFormRow("Name", nameInput);
		panel.addFormRow("Category", parentInput);
		panel.addFormRow("Topic", topicInput);
		panel.addFormRow("Slowmode Duration (seconds)", slowmodeInput);
		panel.addCheckBox(nsfwCheckBox);
		panel.addCheckBox(announcementCheckBox);
		panel.addBottomButtons(
			SwingUtils.button("Send to Discord", this::onSendPressed),
			SwingUtils.button("Cancel", this::dispose)
		);

		return panel;
	}

	private void parentDropdownRefresh() {
		dataManager.cache.values().stream()
			.filter(c -> (c.type() == Channel.Type.GUILD_CATEGORY))
			.sorted((a, b) -> a.position().compareTo(b.position()))
			.map(GuildChannelNameWrapper::new)
			.forEach(parentInput::addItem);
	}

	private void onSendPressed() {
		final TextChannel.Payload payload;

		/* name (required) */ {
			final var name = nameInput.getText();
			if (name == null || name.isBlank()) {
				JOptionPane.showMessageDialog(this, "Name is blank!");
				return;
			}
			payload = new TextChannel.Payload(nameInput.getText());
		}

		/* parent channel */ {
			final var selectedParent = (GuildChannelNameWrapper) parentInput.getSelectedItem();
			if (selectedParent != null) {
				payload.parentId = selectedParent.channel.id();
			}
		}

		/* slowmode duration */ {
			final var slowmodeDuration = (short) slowmodeInput.getValue();
			if (slowmodeDuration > 0) {
				payload.rateLimitPerUser = slowmodeDuration;
			}
		}

		/* topic */ {
			final var topic = topicInput.getText();
			if (topic != null && !topic.isBlank()) {
				payload.topic = topic;
			}
		}

		// convert to announcement channel
		if (announcementCheckBox.isSelected()) {
			payload.type = Channel.Type.GUILD_ANNOUNCEMENT;
		}

		// age-restricted channel
		if (nsfwCheckBox.isSelected()) {
			payload.nsfw = true;
		}

		// send payload to listeners
		if (editRequest != null) {
			editRequest.payload = payload;
			editRequested.accept(editRequest);
		} else {
			createRequested.accept(payload);
		}

		dispose();
	}
}
