package test_bot;

import java.util.List;

import org.json.simple.JSONArray;

import discord.client.DiscordClient;
import discord.managers.CommandManager;
import discord.structures.Application.Command;
import discord.util.Util;

public class CommandSetter {

	public static List<Command.Payload> getCommands() {
		final var commands = new JSONArray();
		var command = new Command.Payload("test_string", "testing string option");
		command.options.add(new Command.Option.Payload(Command.Option.Type.STRING, "string", "enter string"));
		commands.add(command);
		command = new Command.Payload("test_2_integers", "this has 2 integer options, one required");
		command.options.add(new Command.Option.Payload(Command.Option.Type.INTEGER, "int1", "enter integer 1", true));
		command.options.add(new Command.Option.Payload(Command.Option.Type.INTEGER, "int2", "enter integer 2"));
		commands.add(command);
		return commands;
	}
	
	public static void main(String[] __) {
		final var client = new DiscordClient.Bot();
		client.api.setToken(Util.readFile("./token"));
		client.commands = new CommandManager(client);
		client.commands.set(getCommands()).thenAccept(System.out::println);
	}

}
