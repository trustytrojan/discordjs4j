package discord.structures.interactions;

import java.util.concurrent.CompletableFuture;

import discord.enums.InteractionCallbackType;
import discord.structures.Embed;
import discord.structures.payloads.InteractionReplyMessagePayload;

public interface RepliableInteraction {

	default CompletableFuture<Void> deferReply() {
		return _reply(InteractionCallbackType.DeferredChannelMessageWithSource, null);
	}

	default CompletableFuture<Void> reply(String content) {
		final var payload = new InteractionReplyMessagePayload();
		payload.setContent(content);
		return reply(payload);
	}

	default CompletableFuture<Void> replyEphemeral(String content) {
		final var payload = new InteractionReplyMessagePayload();
		payload.setContent(content);
		payload.setEphemeral(true);
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
		payload.setEphemeral(true);
		return reply(payload);
	}

	default CompletableFuture<Void> reply(InteractionReplyMessagePayload payload) {
		return _reply(InteractionCallbackType.ChannelMessageWithSource, payload);
	}

	public CompletableFuture<Void> _reply(InteractionCallbackType type, InteractionReplyMessagePayload payload);

}
