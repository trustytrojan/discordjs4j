package guild_channel_manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import discord.client.BotDiscordClient;
import discord.managers.guild.GuildChannelManager;
import discord.util.Util;
import swing_extensions.GridBagPanel;
import swing_extensions.LoadingDialog;
import swing_extensions.SwingUtils;

final class GuildChannelManagerGUI extends JFrame {
	private final GuildChannelManager dataManager;
	private final GuildChannelsTable table = new GuildChannelsTable();
	private final LoadingDialog loadingDialog = new LoadingDialog(this, "Waiting for Discord...");

	GuildChannelManagerGUI(GuildChannelManager dataManager) {
		super("Guild Channel Manager GUI");
		this.dataManager = dataManager;

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
		tablePanel.setPreferredSize(new Dimension(500, 500));
		panel.add(tablePanel, c);

		c = GridBagPanel.constraintsInsets5();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 1;
		panel.add(SwingUtils.button("Refresh", this::refreshClicked), c);

		c.gridy = 1;
		panel.add(SwingUtils.button("Create", this::createClicked), c);

		c.gridy = 2;
		panel.add(SwingUtils.button("Edit", this::editClicked), c);

		c.gridy = 3;
		panel.add(SwingUtils.button("Delete", this::deleteClicked), c);

		c = new GridBagConstraints();
		c.gridy = 3;
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(new JPanel(), c);

		return panel;
	}

	private void refreshClicked() {
		table.clear();
		loadingDialog.setVisible(true);
		dataManager.refreshCache().thenRunAsync(() -> {
			dataManager.cache.values().forEach(table::addRow);
			loadingDialog.dispose();
		});
	}

	private void createClicked() {

	}

	private void editClicked() {

	}

	private void deleteClicked() {

	}

	public static void main(String[] args) {
		final var client = new BotDiscordClient();
		client.api.setToken(Util.readFile("tokens/java-bot"));
		client.fetchApplication().join();
		final var guild = client.guilds.fetch("1094436869531504733").join();
		new GuildChannelManagerGUI(guild.channels);
	}
}
