package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Message;
import discord.structures.channels.TextBasedChannel;
import discord.structures.payloads.MessagePayload;
import discord.util.DiscordObjectMap;
import simple_json.JSON;
import simple_json.JSONObject;

public class MessageManager extends DataManager<Message> {

	public final TextBasedChannel channel;

	public MessageManager(DiscordClient client, TextBasedChannel channel) {
		super(client);
		this.channel = channel;
	}

	@Override
	public Message cacheNew(JSONObject data) {
		return cache(new Message(client, data));
	}

	public CompletableFuture<Message> create(MessagePayload payload) {
		final var path = "/channels/" + channel.id() + "/messages";
		final var messageData = payload.toJSONString();
		return CompletableFuture.supplyAsync(() -> {
			final var responseData = client.api.post(path, messageData);
			return new Message(client, JSON.parseObject(responseData));
		});
	}

	public CompletableFuture<Void> delete(String id) {
		final var path = "/channels/" + channel.id() + "/messages/" + id;
		return CompletableFuture.runAsync(() -> client.api.delete(path));
	}

	@Override
	public Message fetch(String id, boolean force) {
		return super.fetch(id, "/channels/" + channel.id() + "/messages/" + id, force);
	}

	public DiscordObjectMap<Message> fetch() {
		final var path = "/channels/" + channel.id() + "/messages";
		final var data = JSON.parseObjectArray(client.api.get(path));
		final var messages = new DiscordObjectMap<Message>();
		for (final var obj : data) {
			final var message = new Message(client, obj);
			messages.put(message);
		}
		return messages;
	}

}
