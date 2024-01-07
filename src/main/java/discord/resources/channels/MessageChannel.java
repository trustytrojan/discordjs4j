package discord.resources.channels;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.managers.MessageManager;
import discord.resources.Message;
import discord.structures.Embed;

public interface MessageChannel extends Channel {
	MessageManager getMessageManager();

	default CompletableFuture<Message> send(final Message.Payload payload) {
		return getMessageManager().create(payload);
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
