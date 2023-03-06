package discord.client;

import discord.enums.GatewayIntent;
import discord.managers.UserManager;
import discord.managers.ChannelManager;
import discord.managers.GuildManager;
import discord.structures.AuditLogEntry;
import discord.structures.ClientUser;
import discord.structures.Guild;
import discord.structures.Message;
//import discord.structures.Presence;
import discord.structures.channels.Channel;

public abstract class DiscordClient {

	public final APIClient api = new APIClient();
	public final GatewayClient gateway = new GatewayClient(this);

	public final UserManager users = new UserManager(this);
	public final ChannelManager channels = new ChannelManager(this);
	public final GuildManager guilds = new GuildManager(this);

	public final ClientEvent<Void> ready = new ClientEvent<>();
	public final ClientEvent<Guild> guildCreate = new ClientEvent<>();
	public final ClientEvent<Guild> guildUpdate = new ClientEvent<>();
	public final ClientEvent<Guild> guildDelete = new ClientEvent<>();
	public final ClientEvent<AuditLogEntry> auditLogEntryCreate = new ClientEvent<>();
	public final ClientEvent<Channel> channelCreate = new ClientEvent<>();
	public final ClientEvent<Channel> channelUpdate = new ClientEvent<>();
	public final ClientEvent<Channel> channelDelete = new ClientEvent<>();
	public final ClientEvent<Message> messageCreate = new ClientEvent<>();
	public final ClientEvent<Message> messageUpdate = new ClientEvent<>();
	public final ClientEvent<Message> messageDelete = new ClientEvent<>();

	public ClientUser user;

	public void login(String token, GatewayIntent[] intents) throws Exception {
		api.setToken(token);

		gateway.login(token, intents);
	}

}
