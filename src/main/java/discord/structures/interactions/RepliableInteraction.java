package discord.structures.interactions;

import java.util.concurrent.CompletableFuture;

import discord.enums.InteractionCallbackType;
import discord.structures.Embed;
import discord.structures.InteractionReplyMessagePayload;

public interface RepliableInteraction {
	CompletableFuture<Void> _reply(InteractionCallbackType type, InteractionReplyMessagePayload payload);

	default CompletableFuture<Void> deferReply() {
		return _reply(InteractionCallbackType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE, null);
	}

	default CompletableFuture<Void> reply(String content) {
		final var payload = new InteractionReplyMessagePayload();
		payload.content = content;
		return reply(payload);
	}

	default CompletableFuture<Void> replyEphemeral(String content) {
		final var payload = new InteractionReplyMessagePayload();
		payload.content = content;
		payload.ephemeral = Boolean.TRUE;
		return reply(payload);
	}

	default CompletableFuture<Void> reply(Embed... embeds) {
		final var payload = new InteractionReplyMessagePayload();
		payload.addEmbeds(embeds);
		return reply(payload);
	}

	default CompletableFuture<Void> replyEphemeral(Embed... embeds) {
		final var payload = new InteractionReplyMessagePayload();
		payload.addEmbeds(embeds);
		payload.ephemeral = Boolean.TRUE;
		return reply(payload);
	}

	default CompletableFuture<Void> reply(InteractionReplyMessagePayload payload) {
		return _reply(InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE, payload);
	}
}
