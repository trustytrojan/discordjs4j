package discord.managers;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.Message;
import discord.resources.channels.MessageChannel;
import discord.util.Util;
import sj.SjObject;

public class MessageManager extends ResourceManager<Message> {
	public final MessageChannel channel;

	public MessageManager(DiscordClient client, MessageChannel channel) {
		super(client, "/channels/" + channel.id() + "/messages");
		this.channel = Objects.requireNonNull(channel);
	}

	@Override
	public Message construct(SjObject data) {
		return new Message(client, channel, data);
	}

	public CompletableFuture<Message> create(Message.Payload payload) {
		return client.api.post(basePath, payload.toJsonString()).thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Message> edit(String id, Message.Payload payload) {
		return client.api.patch(basePath + id, payload.toJsonString()).thenApply(r -> cache(r.toJsonObject()));
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(basePath + id).thenRun(Util.NO_OP);
	}

	/*
	 * https://discord.com/developers/docs/resources/channel#get-channel-messages
	 */

	private static enum GetManyFilter { AROUND, BEFORE, AFTER }

	private CompletableFuture<List<Message>> getMany(GetManyFilter filter, String id, int limit) {
		final var sb = new StringBuilder(basePath);
		final boolean filterProvided = (filter != null),
					  limitProvided = (limit > 0);
		if (filterProvided || limitProvided) {
			sb.append('?');
			final Runnable appendFilterParam = () -> sb.append(filter.name().toLowerCase() + '=').append(id);
			final Runnable appendLimitParam = () -> sb.append("limit=").append(limit);
			if (filterProvided && limitProvided) {
				appendFilterParam.run();
				sb.append('&');
				appendLimitParam.run();
			}
			else if (filterProvided) appendFilterParam.run();
			else if (limitProvided) appendLimitParam.run();
		}
		return client.api.get(sb.toString()).thenApply(r -> r.toJsonObjectArray().stream().map(this::cache).toList());
	}

	public CompletableFuture<List<Message>> getMany() {
		return getMany(null, null, 0);
	}

	public CompletableFuture<List<Message>> getMany(int limit) {
		return getMany(null, null, limit);
	}

	public CompletableFuture<List<Message>> getManyAround(String id, int limit) {
		return getMany(GetManyFilter.AROUND, id, limit);
	}

	public CompletableFuture<List<Message>> getManyAround(String id) {
		return getMany(GetManyFilter.AROUND, id, 0);
	}
	
	public CompletableFuture<List<Message>> getManyBefore(String id, int limit) {
		return getMany(GetManyFilter.BEFORE, id, limit);
	}

	public CompletableFuture<List<Message>> getManyBefore(String id) {
		return getMany(GetManyFilter.BEFORE, id, 0);
	}

	public CompletableFuture<List<Message>> getManyAfter(String id, int limit) {
		return getMany(GetManyFilter.AFTER, id, limit);
	}

	public CompletableFuture<List<Message>> getManyAfter(String id) {
		return getMany(GetManyFilter.AFTER, id, 0);
	}

	@Override
	public CompletableFuture<Void> refreshCache() {
		throw new UnsupportedOperationException();
	}
}
