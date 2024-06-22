import java.util.Map;
import java.util.function.BiConsumer;

import discord.client.UserDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.Message;
import discord.util.Util;

public final class SelfBot extends UserDiscordClient {
	public static void main(final String[] args) throws Exception {
		new SelfBot(Util.readFile("tokens/main"));
	}

	private SelfBot(final String token) {
		super(token, true);
		gateway.connectAndIdentify(
			GatewayIntent.GUILDS,
			GatewayIntent.GUILD_MESSAGES
		);
	}

	@Override
	protected void onReady() {
		System.out.println("Logged in as " + clientUser.getTag() + '!');
	}

	private static final String PREFIX = "!";

	@Override
	protected void onMessageCreate(final Message message) {
		// Only allow this user to execute commands
		if (!message.getAuthorId().equals(clientUser.getId()))
			return;
		final var args = message.getContent().split(" ");
		if (!args[0].startsWith(PREFIX))
			return;
		final var command = args[0].substring(1);
		try { COMMAND_HANDLERS.get(command).accept(message, args); }
		catch (final Exception e) { message.reply("**this is an error**```" + e.getMessage() + "```"); }
	}

	private static final Map<String, BiConsumer<Message, String[]>> COMMAND_HANDLERS = Map.of(
		"roles", (final var message, final var args) -> {
			final var guild = message.getGuild().join();
			if (guild == null)
				return;
			guild.roles.refreshCache().join();
			final var formatTemplate = "%-20s %s%n";
			final var sb = new StringBuilder("```" + formatTemplate.formatted("ID", "Name"));
			guild.roles.cache.forEach((id, role) -> sb.append(formatTemplate.formatted(id, role.getName())));
			message.reply(sb.append("```").toString());
		},

		"channels", (final var message, final var args) -> {
			final var guild = message.getGuild().join();
			if (guild == null)
				return;
			guild.channels.refreshCache().join();
			final var formatTemplate = "%-20s %-20s %s%n";
			final var sb = new StringBuilder("```" + formatTemplate.formatted("ID", "Type", "Name"));
			guild.channels.cache.forEach(
				(id, channel) -> sb.append(formatTemplate.formatted(id, channel.getType(), channel.getName()))
			);
			message.reply(sb.append("```").toString());
		}
	);
}