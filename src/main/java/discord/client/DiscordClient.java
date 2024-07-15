package discord.client;

import java.util.List;

import discord.managers.ChannelManager;
import discord.managers.GuildManager;
import discord.managers.UserManager;
import discord.resources.ClientUser;
import discord.resources.Message;
import discord.resources.User;
import discord.resources.channels.Channel;
import discord.resources.guilds.Guild;
import discord.structures.Activity;
import discord.structures.AuditLogEntry;
import discord.structures.ClientStatus;
import discord.util.Logger;

public sealed class DiscordClient permits BotDiscordClient, UserDiscordClient {
	public final APIClient api;
	public final GatewayClient gateway;

	public final UserManager users = new UserManager(this);
	public final ChannelManager channels = new ChannelManager(this);
	public final GuildManager guilds = new GuildManager(this);

	public final ClientUser clientUser;

	protected DiscordClient(final String token, final boolean bot, final boolean debug) {
		api = new APIClient(token, bot, debug);
		gateway = new GatewayClient(this, token, debug);
		clientUser = users.getClientUser().join();
		if (debug)
			Logger.log("Logged in as: " + clientUser.getTag());
	}

	// Subclasses should override the methods below to receive events.

	protected void onReady() {}
	protected void onChannelCreate(final Channel channel) {}
	protected void onChannelUpdate(final Channel channel) {}
	protected void onChannelDelete(final Channel channel) {}
	protected void onGuildCreate(final Guild guild) {}
	protected void onGuildUpdate(final Guild guild) {}
	protected void onGuildDelete(final Guild guild) {}
	protected void onGuildAuditLogEntryCreate(final AuditLogEntry auditLogEntry) {}
	protected void onMessageCreate(final Message message) {}
	protected void onMessageUpdate(final Message message) {}
	protected void onMessageDelete(final Message message) {}

	/**
	 * A user's presence is their current state on a guild.
	 * This event is sent when a user's presence or info,
	 * such as name or avatar, is updated.
	 * 
	 * @param user User whose presence is being updated
	 * @param guildId ID of the guild
	 * @param status Either "idle", "dnd", "online", or "offline"
	 * @param activities User's current activities
	 * @param clientStatus User's platform-dependent status
	 */
	protected void onPresenceUpdate(
		final User user,
		final String guildId,
		final String status,
		final List<Activity> activities,
		final ClientStatus clientStatus
	) {}
}
