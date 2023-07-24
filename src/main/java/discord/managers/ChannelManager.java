package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.channels.Channel;
import discord.resources.channels.GroupDMChannel;
import discord.resources.channels.GuildChannel;
import discord.util.Util;
import sj.SjObject;

public class ChannelManager extends ResourceManager<Channel> {
	public ChannelManager(DiscordClient client) {
		super(client, "/channels");
	}

	@Override
	public Channel construct(SjObject data) {
		return Channel.construct(client, data);
	}

	public CompletableFuture<GroupDMChannel> editGroupDM(String id, GroupDMChannel.Payload payload) {
		return client.api.patch(pathWithId(id), payload.toJsonString())
			.thenApply(r -> (GroupDMChannel) cache(r.asObject()));
	}

	public CompletableFuture<GuildChannel> editGuildChannel(String id, GuildChannel.Payload payload) {
		return client.api.patch(pathWithId(id), payload.toJsonString())
			.thenApply(r -> (GuildChannel) cache(r.asObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(pathWithId(id)).thenRun(Util.NO_OP);
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		throw new UnsupportedOperationException();
	}
}
