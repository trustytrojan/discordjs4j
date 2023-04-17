package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Message;
import discord.structures.channels.TextBasedChannel;
import discord.util.IdMap;
import discord.util.Util;
import simple_json.JSONObject;

public class MessageManager extends ResourceManager<Message> {
	public final String basePath;
	public final TextBasedChannel channel;

	public MessageManager(final DiscordClient client, final TextBasedChannel channel) {
		super(client);
		cache.setSizeLimit(50);
		this.channel = channel;
		basePath = "/channels/" + channel.id() + "/messages/";
	}

	@Override
	public Message construct(final JSONObject data) {
		return new Message(client, data);
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

	@Override
	public CompletableFuture<Message> fetch(final String id, final boolean force) {
		return super.fetch(id, basePath + id, force);
	}

	public CompletableFuture<IdMap<Message>> fetch() {
		final var messages = new IdMap<Message>();
		return client.api.get(basePath).thenApplyAsync((final var r) -> {
			r.toJSONObjectArray().forEach((final var o) -> messages.put(cache(o)));
			return messages;
		});
	}
}
