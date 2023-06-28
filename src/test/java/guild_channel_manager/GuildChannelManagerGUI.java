package guild_channel_manager;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import discord.client.BotDiscordClient;
import discord.managers.guild.GuildChannelManager;
import discord.util.Util;
import swing_extensions.GridBagPanel;
import swing_extensions.LoadingDialog;
import swing_extensions.SwingUtils;

final class GuildChannelManagerGUI extends JFrame {
	private final GuildChannelManager channelManager;
	private final GuildChannelsTable table = new GuildChannelsTable();
	private final LoadingDialog loadingDialog = new LoadingDialog(this, "Waiting for Discord...");

	private final GuildChannelDialog gcDialog = new GuildChannelDialog(this);
	private final TextChannelDialog tcDialog;
	private final CategoryChannelDialog ccDialog;

	private final JButton
			refreshButton = SwingUtils.button("Refresh", this::refreshClicked),
			createButton = SwingUtils.button("Create", gcDialog::showCreate),
			editButton = SwingUtils.button("Edit", this::editClicked),
			deleteButton = SwingUtils.button("Delete", this::deleteClicked);

	GuildChannelManagerGUI(GuildChannelManager channelManager) {
		super("Guild Channel Manager GUI");

		this.channelManager = channelManager;
		tcDialog = new TextChannelDialog(this, channelManager);
		ccDialog = new CategoryChannelDialog(this, channelManager);

		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				final var count = table.getSelectedRowCount();
				deleteButton.setEnabled(count > 0);
				editButton.setEnabled(count == 1);
			}
		});

		setupListeners();

		setContentPane(createMainPanel());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		validate();
		pack();
		setVisible(true);
	}

	private JPanel createMainPanel() {
		final var panel = new GridBagPanel();

		var c = GridBagPanel.constraintsInsets5();
		c.weightx = 1;
		c.weighty = 1;
		c.gridheight = 5; // # of buttons below + 1
		final var tablePanel = SwingUtils.tableWithHeaders(table);
		tablePanel.setPreferredSize(new Dimension(800, 500));
		panel.add(tablePanel, c);

		c = GridBagPanel.constraintsInsets5();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 1;
		panel.add(refreshButton, c);

		c.gridy = 1;
		panel.add(createButton, c);

		c.gridy = 2;
		panel.add(editButton, c);

		c.gridy = 3;
		panel.add(deleteButton, c);

		c = new GridBagConstraints();
		c.gridy = 3;
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(new JPanel(), c);

		return panel;
	}

	private void setupListeners() {
		gcDialog.createRequested = (type) -> {
			switch (type) {
				case GUILD_TEXT -> tcDialog.showCreate();
				case GUILD_CATEGORY -> ccDialog.showCreate();
				default -> {}
			}
		};

		tcDialog.createRequested = (payload) -> channelManager.create(payload).thenAccept(table::addRow);

		tcDialog.editRequested = (editRequest) -> channelManager.edit(editRequest.channel.id(), editRequest.payload)
				.thenAccept(channel -> table.setRow(editRequest.rowInTable, channel));

		ccDialog.createRequested = (payload) -> channelManager.create(payload).thenAccept(table::addRow);

		ccDialog.editRequested = (editRequest) -> channelManager.edit(editRequest.channel.id(), editRequest.payload)
				.thenAccept(channel -> table.setRow(editRequest.rowInTable, channel));
	}

	private void refreshClicked() {
		setManagerButtonsEnabled(false);
		table.clear();
		loadingDialog.setVisible(true);
		channelManager.refreshCache().thenRun(() -> {
			channelManager.cache.values().forEach(table::addRow);
			setManagerButtonsEnabled(true);
			loadingDialog.dispose();
		});
	}

	private void setManagerButtonsEnabled(boolean b) {
		createButton.setEnabled(b);
		editButton.setEnabled(b);
		deleteButton.setEnabled(b);
	}

	private void editClicked() {
		final var rowIndex = table.getSelectedRow();
		final var channelId = (String) table.getValueAt(rowIndex, 0);
		channelManager.fetch(channelId).thenAccept(channel -> {
			final var editRequest = new GuildChannelEditRequest(channel, rowIndex);
			switch (channel.type()) {
				case GUILD_TEXT -> tcDialog.showEdit(editRequest);
				case GUILD_CATEGORY -> ccDialog.showEdit(editRequest);
				default -> {}
			}
		});
	}

	private void deleteClicked() {
		final var selectedRows = table.getSelectedRows();
		final var message = "Are you sure you want to delete the " + selectedRows.length + " selected channel(s)?";
		final var option = JOptionPane.showConfirmDialog(this, message,
				"Delete Selected Channels", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (option == JOptionPane.YES_OPTION) {
			String channelId;
			for (final var row : selectedRows) {
				channelId = (String) table.getValueAt(row, 0);
				channelManager.delete(channelId).thenRun(() -> table.removeRow(row));
			}
		}
	}

	public static void main(String[] args) {
		final var client = new BotDiscordClient();
		client.api.setToken(Util.readFile("tokens/java-bot"));
		final var guild = client.guilds.fetch("1094436869531504733").join();
		new GuildChannelManagerGUI(guild.channels);
	}
}
