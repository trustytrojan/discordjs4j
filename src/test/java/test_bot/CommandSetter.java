package test_bot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;

import discord.client.BotDiscordClient;
import discord.enums.ApplicationCommandOptionType;
import discord.managers.ApplicationCommandManager;
import discord.structures.commands.ApplicationCommandOptionPayload;
import discord.structures.commands.ApplicationCommandPayload;
import discord.util.Util;

public class CommandSetter {

	public static List<ApplicationCommandPayload> getCommands() {
		final var commands = new JSONArray();
		var command = new ApplicationCommandPayload("test_string", "testing string option");
		command.options.add(new ApplicationCommandOptionPayload(ApplicationCommandOptionType.String, "string", "enter string"));
		commands.add(command);
		command = new ApplicationCommandPayload("test_2_integers", "this has 2 integer options, one required");
		command.options.add(new ApplicationCommandOptionPayload(ApplicationCommandOptionType.Integer, "int1", "enter integer 1", true));
		command.options.add(new ApplicationCommandOptionPayload(ApplicationCommandOptionType.Integer, "int2", "enter integer 2"));
		commands.add(command);
		return commands;
	}
	
	public static void main(String[] __) {
		final var client = new BotDiscordClient();
		client.api.setToken(Util.readFile("./token"));
		client.commands = new ApplicationCommandManager(client);
		client.commands.set(getCommands()).thenAccept(System.out::println);
	}

}
