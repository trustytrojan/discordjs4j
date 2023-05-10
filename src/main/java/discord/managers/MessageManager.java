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

	public MessageManager(DiscordClient client, TextBasedChannel channel) {
		super(client);
		cache.setSizeLimit(50);
		this.channel = Objects.requireNonNull(channel);
		basePath = "/channels/" + channel.id() + "/messages/";
	}

	@Override
	public Message construct(SjObject data) {
		return new Message(client, data);
	}

	@Override
	public CompletableFuture<Message> fetch(String id, boolean force) {
		return super.fetch(id, basePath + id, force);
	}

	public CompletableFuture<Message> create(Message.Payload payload) {
		return client.api.post(basePath, payload.toJSONString())
				.thenApplyAsync(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Message> edit(String id, Message.Payload payload) {
		return client.api.patch(basePath + id, payload.toJSONString())
				.thenApplyAsync(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(basePath + id).thenRunAsync(Util.DO_NOTHING);
	}

	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath)
				.thenAcceptAsync(r -> r.toJsonObjectArray().forEach(this::cache));
	}
}
