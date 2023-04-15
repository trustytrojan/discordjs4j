package command_manager;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

import discord.client.BotDiscordClient;
import discord.managers.ApplicationCommandManager;

public class CommandManagerGUI extends JFrame {
	private final CommandsTable table = new CommandsTable();
	private final CommandDialog commandCreateDialog = new CommandDialog();
	private final ApplicationCommandManager commandManager;

	CommandManagerGUI(ApplicationCommandManager commandManager) {
		super("DCM Test");

		this.commandManager = commandManager;
		refreshCacheAndTable();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setJMenuBar(createMenuBar());
		add(new JScrollPane(table));
		validate();
		pack();
		setVisible(true);
	}

	private void refreshCacheAndTable() {
		commandManager.refreshCache().thenRun(() -> {
			for (final var command : commandManager.cache) {
				table.addRow(command);
			}
		});
	}

	private JMenuBar createMenuBar() {
		final var commandMenu = new JMenu("Command");
		commandMenu.add("Create...").addActionListener((final var e) -> {
			final var createdCommand = commandCreateDialog.showAndWait();
			if (createdCommand == null) {
				return;
			}
			commandManager.create(createdCommand).thenAccept(table::addRow);
		});

		final var cacheMenu = new JMenu("Cache");
		cacheMenu.add("Refresh").addActionListener((final var e) -> refreshCacheAndTable());

		final var menuBar = new JMenuBar();
		menuBar.add(commandMenu);
		menuBar.add(cacheMenu);

		return menuBar;
	}

	public static void main(String[] args) {
		final var client = new BotDiscordClient();
		client.api.setToken(discord.util.Util.readFile("tokens/java-bot"));
		client.fetchApplication().join();
		new CommandManagerGUI(client.commands);
	}
}
