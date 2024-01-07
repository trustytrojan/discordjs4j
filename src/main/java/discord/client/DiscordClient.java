package discord.client;

import discord.managers.ChannelManager;
import discord.managers.GuildManager;
import discord.managers.UserManager;
import discord.resources.ClientUser;
import discord.resources.Message;
import discord.resources.channels.Channel;
import discord.resources.guilds.Guild;
import discord.structures.AuditLogEntry;

public sealed class DiscordClient permits BotDiscordClient, UserDiscordClient {
	public final APIClient api;
	public final GatewayClient gateway;

	public final UserManager users = new UserManager(this);
	public final ChannelManager channels = new ChannelManager(this);
	public final GuildManager guilds = new GuildManager(this);

	public final ClientUser currentUser;

	protected DiscordClient(String token, boolean bot, boolean debug) {
		api = new APIClient(token, bot, debug);
		gateway = new GatewayClient(this, token, debug);
		currentUser = users.getCurrentUser().join();
	}

	/*
	 * Subclasses should override the below methods to receive events.
	 */

	protected void onReady() {}
	protected void onChannelCreate(Channel channel) {}
	protected void onChannelUpdate(Channel channel) {}
	protected void onChannelDelete(Channel channel) {}
	protected void onGuildCreate(Guild guild) {}
	protected void onGuildUpdate(Guild guild) {}
	protected void onGuildDelete(Guild guild) {}
	protected void onGuildAuditLogEntryCreate(AuditLogEntry auditLogEntry) {}
	protected void onMessageCreate(Message message) {}
	protected void onMessageUpdate(Message message) {}
	protected void onMessageDelete(Message message) {}
}
