package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.util.BetterMap;
import discord.client.DiscordClient;
import discord.structures.Message;
import discord.structures.channels.TextBasedChannel;
import discord.util.JSON;

public class MessageManager extends DataManager<Message> {

	public final TextBasedChannel channel;

	public MessageManager(DiscordClient client, TextBasedChannel channel) {
		super(client);
		this.channel = channel;
	}

	@Override
	public Message forceCache(BetterJSONObject data) {
		return cache(new Message(client, data));
	}

	public CompletableFuture<Message> delete(String id) {
		final var path = String.format("/channels/%s/messages/%s", channel.id(), id);
		return CompletableFuture.supplyAsync(() -> {
			try { client.api.delete(path); return cache.remove(id); }
			catch (Exception e) { e.printStackTrace(); return null; }
		});
	}

	@Override
	public CompletableFuture<Message> fetch(String id, boolean force) {
		final var path = String.format("/channels/%s/messages/%s", channel.id(), id);
		return super.fetch(id, path, force);
	}

	public CompletableFuture<BetterMap<String, Message>> fetch() {
		final var path = String.format("/channels/%s/messages", channel.id());
		return CompletableFuture.supplyAsync(() -> {
			try {
				final var data = JSON.parseObjectArray(client.api.get(path));
				final var messages = new BetterMap<String, Message>();
				for(final var obj : data) {
					final var message = new Message(client, obj);
					messages.put(message.id(), message);
				}
				return messages;
			} catch (Exception e) { e.printStackTrace(); return null; }
		});
	}

}
