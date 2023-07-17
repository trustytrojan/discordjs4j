package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.Guild;
import discord.resources.channels.GuildChannel;
import sj.SjObject;

public class GuildChannelManager extends GuildResourceManager<GuildChannel> {
	public GuildChannelManager(DiscordClient client, Guild guild) {
		super(client, guild);
	}

	@Override
	public GuildChannel construct(SjObject data) {
		return (GuildChannel) client.channels.construct(data);
	}

	@Override
	public CompletableFuture<GuildChannel> fetch(String id, boolean force) {
		return client.channels.fetch(id, force).thenApply(c -> cache((GuildChannel) c));
	}

	public CompletableFuture<GuildChannel> create(GuildChannel.Payload payload) {
		return client.api.post("/guilds/" + guild.id() + "/channels", payload.toJsonString())
			.thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<GuildChannel> edit(String id, GuildChannel.Payload payload) {
		return client.channels.editGuildChannel(id, payload);
	}

	public CompletableFuture<Void> delete(String id) {
		return client.channels.delete(id);
	}

	public CompletableFuture<Void> refreshCache() {
		return client.api.get("/guilds/" + guild.id() + "/channels")
			.thenAccept(r -> r.toJsonObjectArray().forEach(this::cache));
	}
}
