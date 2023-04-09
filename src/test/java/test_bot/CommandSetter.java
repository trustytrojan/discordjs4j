package test_bot;

import java.util.LinkedList;
import java.util.List;

import discord.client.DiscordClient;
import discord.structures.Application;
import discord.structures.ApplicationCommand;
import discord.structures.ApplicationCommandOption;
import discord.util.Util;

public class CommandSetter {

	public static List<ApplicationCommand.Payload> getCommands() {
		final var commands = new LinkedList<ApplicationCommand.Payload>();

		var command = new ApplicationCommand.Payload("test_string", "testing string option");
		command.addOption(ApplicationCommandOption.Type.STRING, "string", "enter string");
		commands.add(command);

		command = new ApplicationCommand.Payload("test_2_integers", "this has 2 integer options, one required");
		command.addOption(ApplicationCommandOption.Type.INTEGER, "int1", "enter integer 1", true);
		command.addOption(ApplicationCommandOption.Type.INTEGER, "int2", "enter integer 2");
		commands.add(command);

		return commands;
	}

	public static void main(String[] __) {
		final var client = new DiscordClient.Bot();
		client.application = new Application(client, client.api.get("/oauth/applications/@me").toJsonObject());
		client.api.setToken("Bot " + Util.readFile("./token"));
		client.commands.refresh().join();
	}

}
