package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Message;
import discord.structures.channels.TextBasedChannel;
import discord.util.IdMap;
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
			final var createdMessageData = client.api.post(messagesPath(), payload.toString()).toJsonObject();
			return new Message(client, createdMessageData);
		});
	}

	public CompletableFuture<Message> edit(String id, Message.Payload payload) {
		return CompletableFuture.supplyAsync(() -> {
			final var updatedMessageData = client.api.patch(messagesPath(id), payload.toString()).toJsonObject();
			return new Message(client, updatedMessageData);
		});
	}

	public CompletableFuture<Void> delete(String id) {
		return CompletableFuture.runAsync(() -> client.api.delete(messagesPath(id)));
	}

	@Override
	public Message fetch(String id, boolean force) {
		return super.fetch(id, messagesPath(id), force);
	}

	public IdMap<Message> fetch() {
		final var messages = new IdMap<Message>();

		for (final var messageData : client.api.get(messagesPath()).toJsonObjectArray()) {
			messages.put(new Message(client, messageData));
		}

		return messages;
	}

}
