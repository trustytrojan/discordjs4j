package command_manager;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import discord.client.BotDiscordClient;
import discord.managers.ApplicationCommandManager;
import discord.util.Util;

public class CommandManagerGUI extends JFrame {
	private final CommandsTable table = new CommandsTable();
	private final CommandDialog commandDialog = new CommandDialog(this);
	private final LoadingDialog loadingDialog = new LoadingDialog(this);
	private final ApplicationCommandManager commandManager;

	CommandManagerGUI(final ApplicationCommandManager commandManager) {
		super("Discord Command Manager");

		this.commandManager = commandManager;

		commandDialog.createRequested.connect((final var payload) -> {
			commandManager.create(payload).thenAcceptAsync(table::addRow);
		});

		// editRequest final stage: send payload and edit table
		commandDialog.editRequested.connect((final var editRequest) -> {
			// send the edit
			commandManager.edit(editRequest.commandId, editRequest.payload)
				// then edit the row in the table
				.thenAcceptAsync((final var command) -> table.setRow(editRequest.rowInTable, command));
		});

		// editRequest 2nd stage: pack command and send to CommandDialog
		table.editClicked.connect((final var editRequest) -> {
			editRequest.currentCommand = commandManager.fetch(editRequest.commandId).join();
			commandDialog.showEdit(editRequest);
		});

		table.deleteClicked.connect((final var row, final var commandId) -> {
			final var option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this command?");
			if (option != JOptionPane.OK_OPTION) return;
			commandManager.delete(commandId).thenRunAsync(() -> table.removeRow(row));
		});

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setJMenuBar(createMenuBar());
		add(new JScrollPane(table));
		validate();
		pack();
		setVisible(true);
	}

	private void refreshCacheAndTable() {
		table.clear();
		loadingDialog.setVisible(true);
		commandManager.refreshCache().thenRun(() -> {
			commandManager.cache.values().forEach(table::addRow);
			loadingDialog.dispose();
		});
	}

	private JMenuBar createMenuBar() {
		final var commandMenu = new JMenu("Command");
		commandMenu.add("Create...").addActionListener((final var e) -> commandDialog.showCreate());

		final var cacheMenu = new JMenu("Cache");
		cacheMenu.add("Refresh").addActionListener((final var e) -> refreshCacheAndTable());

		final var menuBar = new JMenuBar();
		menuBar.add(commandMenu);
		menuBar.add(cacheMenu);

		return menuBar;
	}

	public static void main(final String[] args) {
		final var client = new BotDiscordClient();
		client.api.setToken(Util.readFile("tokens/java-bot"));
		client.fetchApplication().join();
		new CommandManagerGUI(client.commands);
	}
}
