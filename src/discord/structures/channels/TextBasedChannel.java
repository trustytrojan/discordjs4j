package discord.structures.channels;

import java.util.concurrent.CompletableFuture;

import discord.managers.MessageManager;
import discord.structures.Message;
import discord.structures.MessagePayload;
import discord.structures.embed.Embed;
import discord.util.JSON;

public interface TextBasedChannel extends Channel {

	public MessageManager messages();

	default CompletableFuture<Message> send(String content) {
		return send(new MessagePayload().setContent(content));
	}

	default CompletableFuture<Message> send(Embed embed) {
		return send(new MessagePayload().addEmbed(embed));
	}

	default CompletableFuture<Message> send(MessagePayload payload) {
		final var path = String.format("/channels/%s/messages", id());
		return CompletableFuture.supplyAsync(() -> {
			try {
				final var data = JSON.parseObject(client().api.post(path, payload.toJSONString()));
				final var from_cache = messages().cache.get(data.getString("id"));
				if (from_cache == null) return messages().cache(data);
				return from_cache;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

}
