package discord.structures.channels;

import java.util.concurrent.CompletableFuture;

import discord.managers.MessageManager;
import discord.structures.Message;
import discord.structures.embed.Embed;
import discord.structures.payloads.MessagePayload;

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
		return messages().create(payload);
	}

}
