import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import discord.client.UserDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.Message;
import discord.util.Util;

// Friendly reminder that self/user-botting breaks Discord's TOS.
public final class SelfBot extends UserDiscordClient {
	private static final String PREFIX = "!";

	private static final Map<String, BiConsumer<Message, List<String>>> COMMAND_HANDLERS = Map.of(
		// Echoes all arguments.
		"echo", (final var message, final var args) -> {
			message.reply(String.join(" ", args));
		},

		// Lists all of the roles in the current guild.
		"roles", (final var message, final var args) -> {
			final var guild = message.getGuild().join();

			if (guild == null) {
				message.reply("this is a server-only command!");
				return;
			}

			// Refresh the role cache for the guild
			guild.roles.refreshCache().join();

			// Construct and send a formatted table of roles in a codeblock
			final var template = "%-20s %s%n";
			final var sb = new StringBuilder("```" + template.formatted("ID", "Name"));
			guild.roles.cache.forEach((id, role) -> sb.append(template.formatted(id, role.getName())));
			message.reply(sb.append("```").toString());
		},

		// Lists all of the channels in the current guild.
		"channels", (final var message, final var args) -> {
			final var guild = message.getGuild().join();

			if (guild == null) {
				message.reply("this is a server-only command!");
				return;
			}

			// Refresh the channel cache for the guild
			guild.channels.refreshCache().join();

			// Construct and send a formatted table of channels in a codeblock
			final var template = "%-20s %-20s %s%n";
			final var sb = new StringBuilder("```" + template.formatted("ID", "Type", "Name"));
			guild.channels.cache.forEach(
				(id, channel) -> sb.append(template.formatted(id, channel.getType(), channel.getName())));
			message.reply(sb.append("```").toString());
		}
	);

	private SelfBot(final String token) throws Exception {
		super(token, false);

		// Connect to the Discord Gateway.
		gateway.tryConnect();

		// Identify ourselves to the Discord Gateway.
		// Most things (like authentication) are handled behind the scenes, but we need to specify our desired intents here.
		// Because we make use of guilds, their messages, and DMs, pass their respective intents.
		gateway.identify(
			GatewayIntent.GUILDS,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.DIRECT_MESSAGES
		);
	}

	@Override
	protected void onReady() {
		System.out.println("Logged in as " + clientUser.getTag() + '!');
	}

	@Override
	protected void onMessageCreate(final Message message) {
		if (!message.getAuthorId().equals(clientUser.getId()))
			// Only allow ourself to execute commands.
			return;

		if (!message.getContent().startsWith(PREFIX))
			// Ignore messages without our command prefix.
			return;

		// Get space-separated arguments for the commands
		final var args = message.getContent().split(" ");

		// Get the command name by skipping the prefix
		final var command = args[0].substring(PREFIX.length());

		try {
			COMMAND_HANDLERS.get(command)
				// Ignore args[0] since that's just the command name.
				.accept(message, List.of(args).subList(1, args.length));
		} catch (final Exception e) {
			message.reply("**this is an error**```" + e.getMessage() + "```");
		}
	}

	public static void main(final String[] args) throws Exception {
		new SelfBot(Util.File.read("tokens/main"));
	}
}