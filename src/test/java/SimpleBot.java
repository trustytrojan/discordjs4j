import java.util.ArrayList;
import java.util.List;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.ApplicationCommand;
import discord.structures.ApplicationCommandOption;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;
import discord.util.Util;

public final class SimpleBot extends BotDiscordClient {
	public static void main(final String[] args) throws Exception {
		new SimpleBot(Util.readFile("tokens/bot"));
	}

	private SimpleBot(final String token) {
		super(token, true);
		gateway.connectAndIdentify(GatewayIntent.GUILDS);
	}

	@Override
	protected void onReady() {
		System.out.println("Logged in as " + clientUser.getTag() + '!');
		// application.commands.set(List.of());
		guilds.get("1184967287439106098").thenAccept(g -> g.commands.set(makeCommands()));
	}

	private List<? extends ApplicationCommand.Payload> makeCommands() {
		final var list = new ArrayList<ApplicationCommand.Payload>();
		list.add(new ApplicationCommand.ChatInputPayload("ping", "ping"));

		final var echo = new ApplicationCommand.ChatInputPayload("echo", "echo");
		echo.options = List.of(
			new ApplicationCommandOption.NonSubcommandPayload(ApplicationCommandOption.Type.STRING, "msg", "msg", true)
		);
		list.add(echo);

		return list;
	}

	@Override
	protected void onInteractionCreate(final Interaction interaction) {
		if (interaction instanceof final ChatInputInteraction cii) {
			switch (cii.commandName) {
				case "ping" -> cii.reply("`" + gateway.getPing() + "ms`");
				case "echo" -> cii.reply(cii.options.getString("msg"));
			}
		}
	}
}
