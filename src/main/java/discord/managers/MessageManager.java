package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Message;
import discord.structures.channels.TextBasedChannel;
import discord.util.IdMap;
import discord.util.Util;
import simple_json.JSONObject;

public class MessageManager extends DataManager<Message> {

	public final TextBasedChannel channel;

	public MessageManager(DiscordClient client, TextBasedChannel channel) {
		super(client);
		this.channel = channel;
	}

	@Override
	public Message construct(JSONObject data) {
		return new Message(client, data);
	}

	private String messagesPath() {
		return "/channels/" + channel.id() + "/messages";
	}

	private String messagesPath(String id) {
		return messagesPath() + '/' + id;
	}

	public CompletableFuture<Message> create(Message.Payload payload) {
		return CompletableFuture.supplyAsync(() -> {
			final var createdMessageData = client.api.post(messagesPath(), payload.toJSONString()).join()
					.toJSONObject();
			return new Message(client, createdMessageData);
		});
	}

	public CompletableFuture<Message> edit(String id, Message.Payload payload) {
		return CompletableFuture.supplyAsync(() -> {
			final var updatedMessageData = client.api.patch(messagesPath(id), payload.toJSONString()).join().toJSONObject();
			return new Message(client, updatedMessageData);
		});
	}

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete(messagesPath(id)).thenRunAsync(Util.DO_NOTHING);
	}

	@Override
	public CompletableFuture<Message> fetch(String id, boolean force) {
		return super.fetch(id, messagesPath(id), force);
	}

	public CompletableFuture<IdMap<Message>> fetch() {
		final var messages = new IdMap<Message>();

		return CompletableFuture.supplyAsync(() -> {
			for (final var messageData : client.api.get(messagesPath()).join().toJSONObjectArray()) {
				cache(messageData);
			}
	
			return messages;
		});
	}

}
