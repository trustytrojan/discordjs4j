package discord.managers;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Message;
import discord.structures.channels.TextBasedChannel;
import discord.util.Util;
import simple_json.SjObject;

public class MessageManager extends ResourceManager<Message> {
	public final String basePath;
	public final TextBasedChannel channel;

	public MessageManager(final DiscordClient client, final TextBasedChannel channel) {
		super(client);
		cache.setSizeLimit(50);
		this.channel = Objects.requireNonNull(channel);
		basePath = "/channels/" + channel.id() + "/messages/";
	}

	@Override
	public Message construct(final SjObject data) {
		return new Message(client, data);
	}

	@Override
	public CompletableFuture<Message> fetch(final String id, final boolean force) {
		return super.fetch(id, basePath + id, force);
	}

	public CompletableFuture<Message> create(final Message.Payload payload) {
		return client.api.post(basePath, payload.toJSONString())
				.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<Message> edit(final String id, final Message.Payload payload) {
		return client.api.patch(basePath + id, payload.toJSONString())
				.thenApplyAsync((final var r) -> cache(r.toJSONObject()));
	}

	public CompletableFuture<Void> delete(final String id) {
		return client.api.delete(basePath + id).thenRunAsync(Util.DO_NOTHING);
	}

	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath)
				.thenAcceptAsync((final var r) -> r.toJSONObjectArray().forEach(this::cache));
	}
}
