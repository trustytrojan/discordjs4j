package discord.structures.channels;

import java.util.concurrent.CompletableFuture;

import discord.managers.MessageManager;
import discord.structures.Embed;
import discord.structures.Message;

public interface TextBasedChannel extends Channel {
	MessageManager messages();

	default CompletableFuture<Message> send(String content) {
		final var payload = new Message.Payload();
		payload.setContent(content);
		return send(payload);
	}

	default CompletableFuture<Message> send(Embed... embeds) {
		final var payload = new Message.Payload();
		payload.addEmbeds(embeds);
		return send(payload);
	}

	default CompletableFuture<Message> send(Message.Payload payload) {
		return messages().create(payload);
	}
}
