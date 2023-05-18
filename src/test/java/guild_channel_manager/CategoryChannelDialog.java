package guild_channel_manager;

import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import discord.managers.guild.GuildChannelManager;
import discord.structures.channels.CategoryChannel;
import discord.structures.channels.GuildChannel;
import discord.structures.channels.Channel;
import swing_extensions.FormPanel;
import swing_extensions.MyDialog;
import swing_extensions.SwingUtils;

public class CategoryChannelDialog extends MyDialog {
	private final JTextField nameInput = new JTextField();
	private final JComboBox<GuildChannelNameWrapper> positionInput = new JComboBox<>();

	private final GuildChannelManager dataManager;
	private GuildChannelEditRequest editRequest;

	public Consumer<GuildChannel.Payload> createRequested;
	public Consumer<GuildChannelEditRequest> editRequested;

	public CategoryChannelDialog(Window owner, GuildChannelManager dataManager) {
		super(owner);
		this.dataManager = dataManager;

		setContentPane(createMainPanel());
		validate();
		pack();
	}

	public void showCreate() {
		editRequest = null;
		clearInputs();
		positionDropdownRefresh();
		setTitle("Create Category");
		setVisible(true);
	}

	public void showEdit(GuildChannelEditRequest editRequest) {
		this.editRequest = editRequest;
		fillInputs((CategoryChannel) editRequest.channel);
		positionDropdownRefresh();
		setTitle("Edit Category - " + editRequest.channel.name());
		setVisible(true);
	}

	private void clearInputs() {
		nameInput.setText(null);
		positionInput.setSelectedIndex(-1);
	}

	private void fillInputs(CategoryChannel channel) {
		nameInput.setText(channel.name());
		positionInput.setSelectedIndex(channel.position().intValue());
	}

	private void positionDropdownRefresh() {
		dataManager.cache.values().stream()
			.filter(c -> (c.type() == Channel.Type.GUILD_CATEGORY))
			.sorted((a, b) -> a.position().compareTo(b.position()))
			.map(GuildChannelNameWrapper::new)
			.forEach(positionInput::addItem);
	}

	private FormPanel createMainPanel() {
		final var panel = new FormPanel();

		panel.addFormRow("Name", nameInput);
		panel.addFormRow("Position", positionInput);
		panel.addBottomButtons(
			SwingUtils.button("Send to Discord", this::sendClicked),
			SwingUtils.button("Cancel", this::dispose)
		);

		return panel;
	}

	private void sendClicked() {
		final CategoryChannel.Payload payload;

		/* name (required) */ {
			final var name = nameInput.getText();
			if (name == null || name.isBlank()) {
				JOptionPane.showMessageDialog(this, "Name is blank!");
				return;
			}
			payload = new CategoryChannel.Payload(nameInput.getText());
		}

		/* position (relative to other categories) */ {
			final var selectedPosition = (GuildChannelNameWrapper) positionInput.getSelectedItem();
			if (selectedPosition != null) {
				payload.position = selectedPosition.channel.position().intValue();
			}
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
