package discord.structures.channels;

import java.util.concurrent.CompletableFuture;

import discord.managers.MessageManager;
import discord.structures.Message;
import discord.structures.MessagePayload;
import discord.structures.embed.Embed;

public interface TextBasedChannel extends Channel {

	public MessageManager messages();

	default CompletableFuture<Message> send(String content) {
		final var payload = new MessagePayload();
		payload.setContent(content);
		return send(payload);
	}

	default CompletableFuture<Message> send(Embed... embeds) {
		final var payload = new MessagePayload();
		payload.addEmbeds(embeds);
		return send(payload);
	}

	default CompletableFuture<Message> send(MessagePayload payload) {
		final var path = String.format("/channels/%s/messages", id());
		return CompletableFuture.supplyAsync(() -> {
			final var data = JSON.parseObject(client().api.post(path, payload.toJSONString()));
			final var from_cache = messages().cache.get(data.getString("id"));
			if (from_cache == null)
				return messages().cache(data);
			return from_cache;
		});
	}

}
