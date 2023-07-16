package discord.resources.channels;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.managers.MessageManager;
import discord.resources.Embed;
import discord.resources.Message;

public interface TextBasedChannel extends Channel {
	MessageManager messages();

	default CompletableFuture<Message> send(final Message.Payload payload) {
		return messages().create(payload);
	}

	default CompletableFuture<Message> send(final String content) {
		final var payload = new Message.Payload();
		payload.content = content;
		return send(payload);
	}

	default CompletableFuture<Message> send(final Embed... embeds) {
		final var payload = new Message.Payload();
		payload.embeds = List.of(embeds);
		return send(payload);
	}

	default CompletableFuture<Message> send(final String content, final Embed... embeds) {
		final var payload = new Message.Payload();
		payload.content = content;
		payload.embeds = List.of(embeds);
		return send(payload);
	}
}
