package command_manager;

import discord.structures.ApplicationCommand;

public class CommandEditRequest {
	public final String commandId;
	public final int rowInTable;
	public ApplicationCommand currentCommand;
	public ApplicationCommand.Payload payload;

	public CommandEditRequest(final String commandId, final int rowInTable) {
		this.commandId = commandId;
		this.rowInTable = rowInTable;
	}
}
