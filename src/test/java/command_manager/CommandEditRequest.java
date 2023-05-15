package command_manager;

import discord.structures.ApplicationCommand;

final class CommandEditRequest {
	final String commandId;
	final int rowInTable;
	ApplicationCommand currentCommand;
	ApplicationCommand.Payload payload;

	CommandEditRequest(String commandId, int rowInTable) {
		this.commandId = commandId;
		this.rowInTable = rowInTable;
	}
}
