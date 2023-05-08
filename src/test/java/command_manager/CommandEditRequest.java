package command_manager;

import discord.structures.ApplicationCommand;

final class CommandEditRequest {
	final String commandId;
	final int rowInTable;
	ApplicationCommand currentCommand;
	ApplicationCommand.Payload payload;

	CommandEditRequest(final String commandId, final int rowInTable) {
		this.commandId = commandId;
		this.rowInTable = rowInTable;
	}
}
