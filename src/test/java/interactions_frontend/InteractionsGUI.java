package interactions_frontend;

import java.awt.Dimension;

import javax.swing.JFrame;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.util.Util;

final class InteractionsGUI extends JFrame {
	private final InteractionResponseDialog ird = new InteractionResponseDialog(this);
	private final InteractionPanel.List il = new InteractionPanel.List();

	InteractionsGUI(final BotDiscordClient client) {
		super("Interactions Frontend");

		client.interactionCreate.connect(i -> {
			il.add(i, ird);
			pack();
		});

		setMinimumSize(new Dimension(500, 500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(il);
		validate();
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		final var client = new BotDiscordClient();
		client.login(Util.readFile("tokens/java-bot"), new GatewayIntent[] {});
		new InteractionsGUI(client);
	}
}
