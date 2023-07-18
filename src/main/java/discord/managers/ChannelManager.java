package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.channels.Channel;
import discord.resources.channels.GroupDMChannel;
import discord.resources.channels.GuildChannel;
import sj.SjObject;

public class ChannelManager extends ResourceManager<Channel> {
	public ChannelManager(DiscordClient client) {
		super(client);
	}

	@Override
	public Channel construct(SjObject data) {
		return Channel.fromJSON(client, data);
	}

	public CompletableFuture<GroupDMChannel> editGroupDM(String id, GroupDMChannel.Payload payload) {
		return client.api.patch("/channels/" + id, payload.toJsonString())
			.thenApply(r -> (GroupDMChannel) cache(r.toJsonObject()));
	}

	public CompletableFuture<GuildChannel> editGuildChannel(String id, GuildChannel.Payload payload) {
		return client.api.patch("/channels/" + id, payload.toJsonString())
			.thenApply(r -> (GuildChannel) cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete("/channels/" + id).thenAccept(r -> {
			cache.remove(id);
			final var guildId = r.toJsonObject().getString("guild_id");
			if (guildId != null) {
				client.guilds.get(guildId).thenAccept(g -> g.channels.cache.remove(id));
			}
		});
	}

	@Override
	public CompletableFuture<Channel> get(String id, boolean force) {
		return super.get(id, "/channels/" + id, force);
	}

	
}
