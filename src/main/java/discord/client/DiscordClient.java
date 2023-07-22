package discord.client;

import discord.managers.ChannelManager;
import discord.managers.GuildManager;
import discord.managers.UserManager;
import discord.resources.AuditLogEntry;
import discord.resources.CurrentUser;
import discord.resources.Message;
import discord.resources.channels.Channel;
import discord.resources.guilds.Guild;

public abstract class DiscordClient {
	public final APIClient api;
	public final GatewayClient gateway;

	public final UserManager users = new UserManager(this);
	public final ChannelManager channels = new ChannelManager(this);
	public final GuildManager guilds = new GuildManager(this);

	public final CurrentUser user;

	protected DiscordClient(String token, boolean bot) {
		api = new APIClient(token, bot);
		gateway = new GatewayClient(this, token);
		user = users.getCurrentUser().join();
	}

	/*
	 * Subclasses should override the below methods as necessary
	 * if they want to use old-style signal handling.
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
