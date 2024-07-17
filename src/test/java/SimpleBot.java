import java.util.List;

import discord.client.BotDiscordClient;
import discord.resources.ApplicationCommand;
import discord.resources.ApplicationCommand.ChatInputPayload;
import discord.structures.ApplicationCommandOption.NonSubcommandPayload;
import discord.structures.ApplicationCommandOption.Type;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;
import discord.util.Util;

public final class SimpleBot extends BotDiscordClient {
	private static final List<ApplicationCommand.Payload> COMMAND_PAYLOADS = List.of(
		new ChatInputPayload("ping", "ping"),
		new ChatInputPayload("echo", "echo", List.of(
			new NonSubcommandPayload(Type.STRING, "msg", "msg", true)
		))
	);

	private SimpleBot(final String token) throws Exception {
		// Sets our token in the Discord API and Gateway clients, fetches the authenticated user and enables debug logs.
		super(token, true);

		// Register our commands with Discord through the API (gateway not needed).
		// Since we set them on our application as opposed to on a guild, they are available globally.
		application.commands.set(COMMAND_PAYLOADS);

		// Connect to the Discord Gateway.
		gateway.tryConnect();

		// Identify ourselves to the Discord Gateway. This is required before doing anything else with the Gateway.
		// Most things (like authentication) are handled behind the scenes, but we need to specify our desired intents here.
		// Since we don't make use of any resource events, we don't pass any intents.
		gateway.identify();
	}

	@Override
	protected void onReady() {
		System.out.println("Logged in as " + clientUser.getTag() + '!');
	}

	/**
	 * This event fires when a user interacts with our application.
	 * We can catch usage of our chat input commands by checking if
	 * the interaction received is a {@link ChatInputInteraction}.
	 */
	@Override
	protected void onInteractionCreate(final Interaction interaction) {
		if (!(interaction instanceof final ChatInputInteraction cii))
			// Ignore all other interaction types.
			return;

		switch (cii.commandName) {
			case "ping" -> cii.reply("pong! `" + gateway.getPing() + "ms`");
			case "echo" -> cii.reply(cii.options.getString("msg"));
		}
	}

	public static void main(final String[] args) throws Exception {
		new SimpleBot(Util.File.read("tokens/bot"));
	}
}
