package discord.managers.guild;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.channels.CategoryChannel;
import discord.resources.channels.Channel;
import discord.resources.channels.GuildChannel;
import discord.resources.channels.TextChannel;
import discord.resources.channels.VoiceChannel;
import discord.resources.guilds.Guild;
import sj.SjObject;

/**
 * This class relies mostly on ChannelManager for API calls, except for
 * getAll().
 */
public class GuildChannelManager extends GuildResourceManager<GuildChannel> {
	public GuildChannelManager(DiscordClient client, Guild guild) {
		super(client, guild, "/channels");
	}

	@Override
	public GuildChannel construct(SjObject data) {
		return (GuildChannel) client.channels.construct(data);
	}

	@Override
	public CompletableFuture<GuildChannel> get(String id, boolean force) {
		return client.channels.get(id, force).thenApply(c -> cache((GuildChannel) c));
	}

	public CompletableFuture<GuildChannel> create(GuildChannel.Payload payload) {
		return client.api.post(basePath, payload.toJsonString()).thenApply(r -> cache(r.asObject()));
	}

	public CompletableFuture<GuildChannel> edit(String id, GuildChannel.Payload payload) {
		return client.channels.editGuildChannel(id, payload);
	}

	public CompletableFuture<Void> delete(String id) {
		return client.channels.delete(id);
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath).thenAccept(this::cacheNewDeleteOld);
	}

	public List<CategoryChannel> getCategoryChannels() {
		return cache.values().stream()
			.filter(c -> c.getType() == Channel.Type.GUILD_CATEGORY)
			.map(c -> (CategoryChannel) c)
			.toList();
	}

	public List<TextChannel> getTextChannels() {
		return cache.values().stream()
			.filter(c -> c.getType() == Channel.Type.GUILD_TEXT)
			.map(c -> (TextChannel) c)
			.toList();
	}

	public List<VoiceChannel> getVoiceChannels() {
		return cache.values().stream()
			.filter(c -> c.getType() == Channel.Type.GUILD_VOICE)
			.map(c -> (VoiceChannel) c)
			.toList();
	}
}
