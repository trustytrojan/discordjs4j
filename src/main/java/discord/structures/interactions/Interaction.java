package discord.structures.interactions;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.APIClient.JsonResponse;
import discord.client.BotDiscordClient;
import discord.resources.GuildMember;
import discord.resources.Message;
import discord.resources.User;
import discord.resources.channels.MessageChannel;
import discord.resources.guilds.Guild;
import discord.structures.Embed;
import discord.structures.components.ActionRow;
import discord.util.Util;
import sj.SjObject;

public abstract class Interaction {
	public static enum Type {
		PING,
		APPLICATION_COMMAND,
		MESSAGE_COMPONENT,
		APPLICATION_COMMAND_AUTOCOMPLETE,
		MODAL_SUBMIT;

		public static Type resolve(final int value) {
			return Type.values()[value - 1];
		}
	}

	private static enum CallbackType {
		PONG(1),
		CHANNEL_MESSAGE_WITH_SOURCE(4),
		DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE(5),
		DEFERRED_UPDATE_MESSAGE(6),
		UPDATE_MESSAGE(7),
		APPLICATION_COMMAND_AUTOCOMPLETE_RESULT(8),
		MODAL(9);

		public final int value;

		private CallbackType(final int value) {
			this.value = value;
		}
	}

	public static class Response extends Message.Payload {
		public boolean ephemeral;

		public Response() {}

		public Response(final boolean ephemeral) {
			this.ephemeral = ephemeral;
		}

		@Override
		public String toJsonString() {
			final var obj = toJsonObject();
			if (ephemeral)
				obj.put("ephemeral", true);
			return obj.toString();
		}
	}

	public static Interaction construct(final BotDiscordClient client, final SjObject data) {
		return switch (Type.resolve(data.getShort("type"))) {
			case APPLICATION_COMMAND -> new ChatInputInteraction(client, data);
			case MESSAGE_COMPONENT -> new MessageComponentInteraction(client, data);
			default -> null;
		};
	}

	protected final BotDiscordClient client;
	protected final SjObject innerData;
	private final String id, token;
	public final Type type;
	public final String channelId, guildId, userId, originalResponsePath;

	protected Interaction(final BotDiscordClient client, final SjObject data) {
		this.client = Objects.requireNonNull(client);
		innerData = data.getObject("data");
		id = data.getString("id");
		type = Type.resolve(data.getShort("type"));
		token = data.getString("token");
		channelId = data.getObject("channel").getString("id");
		guildId = data.getString("guild_id");
		userId = (guildId == null)
			? data.getObject("user").getString("id")
			: data.getObject("member").getObject("user").getString("id");
		originalResponsePath = "/webhooks/" + client.application.getId() + '/' + token + "/messages/@original";
	}

	public boolean inGuild() {
		return guildId != null;
	}

	public CompletableFuture<Guild> getGuild() {
		return client.guilds.get(guildId);
	}

	public CompletableFuture<MessageChannel> getChannel() {
		return client.channels.get(channelId).thenApply(c -> (MessageChannel) c);
	}

	public CompletableFuture<GuildMember> getMember() {
		return getGuild().thenCompose(g -> g.members.get(userId));
	}

	public CompletableFuture<User> getUser() {
		return client.users.get(userId);
	}

	private CompletableFuture<JsonResponse> createResponse(final CallbackType type, final Response payload) {
		final var path = "/interactions/" + id + '/' + token + "/callback";
		final var data = new SjObject();
		data.put("type", type.value);
		if (payload != null)
			data.put("data", payload);
		return client.api.post(path, data.toJsonString());
	}

	public CompletableFuture<Message> getOriginalResponse() {
		return client.api.get(originalResponsePath)
			.thenApply(r -> new Message(client, r.asObject()));
	}

	public CompletableFuture<Message> editOriginalResponse(final Message.Payload payload) {
		return client.api.patch(originalResponsePath, payload.toJsonString())
			.thenApply(r -> new Message(client, r.asObject()));
	}

	public CompletableFuture<Void> deleteOriginalResponse() {
		return client.api.delete(originalResponsePath).thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> deferResponse() {
		return createResponse(CallbackType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE, null).thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> reply(final Response payload) {
		return createResponse(CallbackType.CHANNEL_MESSAGE_WITH_SOURCE, payload).thenRun(Util.NO_OP);
	}

	public CompletableFuture<Void> reply(final String content) {
		return reply(onlyContent(false, content));
	}

	public CompletableFuture<Void> reply(final Embed... embeds) {
		return reply(onlyEmbeds(false, embeds));
	}

	public CompletableFuture<Void> reply(final String content, final Embed... embeds) {
		return reply(contentAndEmbeds(false, content, embeds));
	}

	public CompletableFuture<Void> reply(final String content, final ActionRow... rows) {
		return reply(contentAndActionRows(false, content, rows));
	}

	public CompletableFuture<Void> replyEphemeral(final String content) {
		return reply(onlyContent(true, content));
	}

	public CompletableFuture<Void> replyEphemeral(final Embed... embeds) {
		return reply(onlyEmbeds(true, embeds));
	}

	public CompletableFuture<Void> replyEphemeral(final String content, final Embed... embeds) {
		return reply(contentAndEmbeds(true, content, embeds));
	}

	public CompletableFuture<Void> replyEphemeral(final String content, final ActionRow... rows) {
		return reply(contentAndActionRows(true, content, rows));
	}

	public CompletableFuture<Message> createFollowupMessage(final Message.Payload payload) {
		final var path = "/webhooks/" + client.application.getId() + '/' + token;
		return client.api.post(path, payload.toJsonString())
			.thenApply(r -> new Message(client, r.asObject()));
	}

	public CompletableFuture<Message> followUp(final String content) {
		return createFollowupMessage(onlyContent(false, content));
	}

	/**
	 * @warning Your first follow up message cannot be ephemeral.
	 */
	public CompletableFuture<Message> followUpEphemeral(final String content) {
		return createFollowupMessage(onlyContent(true, content));
	}

	private Response onlyContent(final boolean ephemeral, final String content) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		return payload;
	}

	private Response onlyEmbeds(final boolean ephemeral, final Embed[] embeds) {
		final var payload = new Response(ephemeral);
		payload.embeds = List.of(embeds);
		return payload;
	}

	private Response contentAndEmbeds(final boolean ephemeral, final String content, final Embed[] embeds) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		payload.embeds = List.of(embeds);
		return payload;
	}

	private Response contentAndActionRows(final boolean ephemeral, final String content, final ActionRow[] rows) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		payload.components = List.of(rows);
		return payload;
	}
}
