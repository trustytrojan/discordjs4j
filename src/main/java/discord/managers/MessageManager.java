package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Message;
import discord.structures.channels.TextBasedChannel;
import discord.util.DiscordResourceMap;
import simple_json.JSON;
import simple_json.JSONObject;

public class MessageManager extends DataManager<Message> {

	public final TextBasedChannel channel;

	public MessageManager(DiscordClient client, TextBasedChannel channel) {
		super(client);
		this.channel = channel;
	}

	@Override
	public Message cache(JSONObject data) {
		return cache(new Message(client, data));
	}

	private String messagesPath() {
		return "/channels/" + channel.id() + "/messages";
	}

	private String messagesPath(String id) {
		return messagesPath() + '/' + id;
	}

	public CompletableFuture<Message> create(Message.Payload payload) {
		return CompletableFuture.supplyAsync(() -> {
			final var createdMessageData = client.api.post(messagesPath(), payload.toString());
			return new Message(client, JSON.parseObject(createdMessageData));
		});
	}

	public CompletableFuture<Message> edit(String id, Message.Payload payload) {
		return CompletableFuture.supplyAsync(() -> {
			final var updatedMessageData = client.api.patch(messagesPath(id), payload.toString());
			return new Message(client, JSON.parseObject(updatedMessageData));
		});
	}

	public CompletableFuture<Void> delete(String id) {
		return CompletableFuture.runAsync(() -> client.api.delete(messagesPath(id)));
	}

	@Override
	public Message fetch(String id, boolean force) {
		return super.fetch(id, messagesPath(id), force);
	}

	public DiscordResourceMap<Message> fetch() {
		final var messages = new DiscordResourceMap<Message>();

		for (final var messageData : JSON.parseObjectArray(client.api.get(messagesPath()))) {
			messages.put(new Message(client, messageData));
		}

		return messages;
	}

}
