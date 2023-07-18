package discord.managers;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.Message;
import discord.resources.channels.TextBasedChannel;
import discord.util.Util;
import sj.SjObject;

public class MessageManager extends ResourceManager<Message> {
	public final String basePath;
	public final TextBasedChannel channel;

	public MessageManager(DiscordClient client, TextBasedChannel channel) {
		super(client);
		this.channel = Objects.requireNonNull(channel);
		basePath = "/channels/" + channel.id() + "/messages/";
	}

	@Override
	public Message construct(SjObject data) {
		return new Message(client, data);
	}

	@Override
	public CompletableFuture<Message> get(String id, boolean force) {
		return super.get(id, basePath + id, force);
	}

	public CompletableFuture<Message> create(Message.Payload payload) {
		return client.api.post(basePath, payload.toJsonString())
				.thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Message> edit(String id, Message.Payload payload) {
		return client.api.patch(basePath + id, payload.toJsonString())
				.thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(basePath + id).thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> refreshCache() {
		return client.api.get(basePath).thenAccept(r -> r.toJsonObjectArray().forEach(this::cache));
	}
}
