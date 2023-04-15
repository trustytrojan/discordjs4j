package command_manager;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

import discord.client.BotDiscordClient;
import discord.managers.ApplicationCommandManager;
import discord.structures.ApplicationCommand;

public class CommandManagerGUI extends JFrame {
	private final CommandsTable table = new CommandsTable();
	private final CommandDialog commandCreateDialog = new CommandDialog(this);
	private final LoadingDialog loadingDialog = new LoadingDialog(this);
	private final ApplicationCommandManager commandManager;

	CommandManagerGUI(final ApplicationCommandManager commandManager) {
		super("DCM Test");

		this.commandManager = commandManager;
		refreshCacheAndTable();

		commandCreateDialog.commandCreated.connect(this::commandCreated);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setJMenuBar(createMenuBar());
		add(new JScrollPane(table));
		validate();
		pack();
		setVisible(true);
	}

	private void commandCreated(final ApplicationCommand.Payload commandPayload) {
		commandManager.create(commandPayload).thenAcceptAsync(table::addRow);
	}

	private void refreshCacheAndTable() {
		table.clear();
		loadingDialog.setVisible(true);
		commandManager.refreshCache().thenRun(() -> {
			for (final var command : commandManager.cache) {
				table.addRow(command);
			}
			loadingDialog.setVisible(false);
		});
	}

	private JMenuBar createMenuBar() {
		final var commandMenu = new JMenu("Command");
		commandMenu.add("Create...").addActionListener((e) -> commandCreateDialog.setVisible(true));

		final var cacheMenu = new JMenu("Cache");
		cacheMenu.add("Refresh").addActionListener((final var e) -> refreshCacheAndTable());

		final var menuBar = new JMenuBar();
		menuBar.add(commandMenu);
		menuBar.add(cacheMenu);

		return menuBar;
	}

	public static void main(final String[] args) {
		final var client = new BotDiscordClient();
		client.api.setToken(discord.util.Util.readFile("tokens/java-bot"));
		client.fetchApplication().join();
		new CommandManagerGUI(client.commands);
	}
}
