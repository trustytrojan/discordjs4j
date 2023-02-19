package discord.structures.interactions;

import java.util.concurrent.CompletableFuture;

import discord.enums.InteractionCallbackType;
import discord.structures.MessagePayload;
import discord.structures.embed.Embed;

public interface RepliableInteraction {

	default CompletableFuture<Void> deferReply() {
		return _reply(InteractionCallbackType.DeferredChannelMessageWithSource, null);
	}

	default CompletableFuture<Void> reply(String content) {
		return _reply(InteractionCallbackType.ChannelMessageWithSource, new MessagePayload().setContent(content));
	}

	default CompletableFuture<Void> reply(Embed embed) {
		return _reply(InteractionCallbackType.ChannelMessageWithSource, new MessagePayload().addEmbed(embed));
	}

	default CompletableFuture<Void> reply(MessagePayload payload) {
		return _reply(InteractionCallbackType.ChannelMessageWithSource, payload);
	}

	public CompletableFuture<Void> _reply(InteractionCallbackType type, MessagePayload payload);

}
