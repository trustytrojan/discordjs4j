package interactions_frontend;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.OptionResolver;
import discord.structures.interactions.Interaction;
import discord.structures.interactions.MessageComponentInteraction;

final class InteractionPanel extends JPanel {
	private static final Calendar CALENDAR = Calendar.getInstance();
	private static String timeHoursMinutes() {
		return "" + CALENDAR.get(Calendar.HOUR_OF_DAY) + ':' + CALENDAR.get(Calendar.MINUTE);
	}
	private static String fifteenMinutesFromNow() {
		return "" + CALENDAR.get(Calendar.HOUR_OF_DAY) + ':' + (CALENDAR.get(Calendar.MINUTE) + 15);
	}

	private static final Insets INSETS_5 = new Insets(5, 5, 5, 5);

	private static GridBagConstraints defaultConstraints() {
		final var c = new GridBagConstraints();
		c.insets = INSETS_5;
		return c;
	}

	private static void buildCommandStr(final StringBuilder sb, final OptionResolver options) {
		for (final var option : options) {
			switch (option.type) {
				case SUB_COMMAND:
				case SUB_COMMAND_GROUP:
					sb.append(' ' + option.name);
					buildCommandStr(sb, option.options);
					break;
				default:
					sb.append(' ' + option.name + ':' + option.value);
			}
		}
	}

	static class List extends JPanel {
		private int nexty;

		List() {
			setLayout(new GridBagLayout());
		}

		void add(final Interaction i, final InteractionResponseDialog d) {
			final var c = defaultConstraints();
			c.gridy = nexty++;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(new InteractionPanel(i, d), c);
			validate();
		}
	}

	final Interaction interaction;

	private final JLabel label1 = new JLabel();
	private final JLabel label2 = new JLabel();
	private final JLabel statusLabel = new JLabel("Status: Valid for 3 seconds");
	private final JButton button = new JButton("Defer Response");

	private int invalidatedWhenZero = 3;
	private final Timer invalidatedTimer = new Timer(1_000, null);

	InteractionPanel(final Interaction interaction, final InteractionResponseDialog responseDialog) {
		super(new GridBagLayout());

		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

		this.interaction = interaction;

		invalidatedTimer.addActionListener(__ -> {
			if ((--invalidatedWhenZero) == 0) {
				invalidatedTimer.stop();
				button.setEnabled(false);
				button.setText("Invalidated");
				statusLabel.setText("Status: Invalidated");
			} else {
				statusLabel.setText("Status: Valid for " + invalidatedWhenZero + " seconds");
			}
		});

		button.addActionListener(__ -> {
			if (!interaction.hasBeenDeferred()) {
				interaction.deferResponse().thenRunAsync(() -> button.setText("Respond"));
				invalidatedTimer.stop();
				statusLabel.setText("Status: Deferred, valid until " + fifteenMinutesFromNow());
			} else {
				responseDialog.showCreate();
			}
		});

		responseDialog.responseCreated.connect((final var resp) ->
			interaction.createFollowupMessage(resp)
				.thenRunAsync(() -> {
					button.setText("Send Followup Message");
					statusLabel.setText("Status: Responded at " + timeHoursMinutes());
				})
		);

		label2.setForeground(Color.GRAY);
		label2.setText("From: " + interaction.user.tag());

		if (interaction instanceof final ChatInputInteraction i) {
			final var sb = new StringBuilder();
			sb.append("/" + i.commandName);
			buildCommandStr(sb, i.options);
			label1.setText(sb.toString());
		} else if (interaction instanceof final MessageComponentInteraction i) {
			label1.setText(i.customId);
			label2.setText(label2.getText() + "\nMessage: " + i.message.content());
		} else {
			throw new RuntimeException();
		}

		// Layout components

		var c = defaultConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(label1, c);

		c.gridy = 1;
		add(label2, c);

		c.gridy = 2;
		add(statusLabel, c);

		c = defaultConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 1;
		c.gridheight = 3;
		add(button, c);

		invalidatedTimer.start();
	}
}
