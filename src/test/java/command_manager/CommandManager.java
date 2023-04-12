package command_manager;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTree;

import discord.client.BotDiscordClient;
import discord.structures.ApplicationCommand;
import discord.util.Util;

final class CommandManager {
	static void addChildrenFromCommand(JTree tree, ApplicationCommand command) {
		
	}

	public static void main(final String[] args) {
		// Initialize Discord client
		final var client = new BotDiscordClient();
		client.api.setToken(Util.readFile("tokens/java-bot"));

		// Create a JTree with the root node name
		final var tree = new MyTree("Root");

		// Create child nodes for the root node
		final var rootNode = tree.getRootNode();
		rootNode.addChildren("Child 1", "Child 2", "Child 3");

		// Create menu bar and items
		final var menuBar = new JMenuBar();
		final var cacheMenu = menuBar.add(new JMenu("Cache"));
		cacheMenu.add("Refresh...").addActionListener((final var e) -> {
			client.commands.refresh().join();
			for (final var command : client.commands) {

			}
		});

		final var commandMenu = menuBar.add(new JMenu("Command"));
		commandMenu.add("Create...").addActionListener((final var e) -> {

		});

		// Create a JFrame and begin adding components
		final var frame = new JFrame("Tree Example");
		frame.add(tree);

		// Set the JFrame properties
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
