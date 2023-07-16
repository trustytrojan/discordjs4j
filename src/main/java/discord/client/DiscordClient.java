package discord.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.enums.GatewayIntent;
import discord.managers.ChannelManager;
import discord.managers.GuildManager;
import discord.managers.UserManager;
import discord.resources.AuditLogEntry;
import discord.resources.ClientUser;
import discord.resources.Message;
import discord.resources.channels.Channel;
import discord.resources.guilds.CurrentUserGuild;
import discord.resources.guilds.Guild;
import signals.Signal0;
import signals.Signal1;

public abstract class DiscordClient {
	public final APIClient api = new APIClient();
	public final GatewayClient gateway = new GatewayClient(this);

	public final UserManager users = new UserManager(this);
	public final ChannelManager channels = new ChannelManager(this);
	public final GuildManager guilds = new GuildManager(this);

	public final Signal0 ready = new Signal0();
	public final Signal1<Guild> guildCreate = new Signal1<>();
	public final Signal1<Guild> guildUpdate = new Signal1<>();
	public final Signal1<Guild> guildDelete = new Signal1<>();
	public final Signal1<AuditLogEntry> auditLogEntryCreate = new Signal1<>();
	public final Signal1<Channel> channelCreate = new Signal1<>();
	public final Signal1<Channel> channelUpdate = new Signal1<>();
	public final Signal1<Channel> channelDelete = new Signal1<>();
	public final Signal1<Message> messageCreate = new Signal1<>();
	public final Signal1<Message> messageUpdate = new Signal1<>();
	public final Signal1<Message> messageDelete = new Signal1<>();

	public ClientUser user;

	public void login(String token, GatewayIntent[] intents) {
		api.setToken(token);
		gateway.login(token, intents);
	}

	public CompletableFuture<List<CurrentUserGuild>> getCurrentGuilds() {
		return api.get("/users/@me/guilds").thenApply(r -> r.toJsonObjectArray().stream().map(CurrentUserGuild::new).toList());
	}
}
