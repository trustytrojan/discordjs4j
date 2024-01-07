package discord.structures.interactions;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.APIClient.JsonResponse;
import discord.client.BotDiscordClient;
import discord.resources.Embed;
import discord.resources.GuildMember;
import discord.resources.Message;
import discord.resources.User;
import discord.resources.channels.MessageChannel;
import discord.resources.guilds.Guild;
import discord.structures.components.ActionRow;
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
		private final boolean ephemeral;

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
	
	private final String id;
	private final String token;
	
	public final Type type;
	
	public final String channelId;
	public final String guildId;
	public final String userId;
	
	protected final SjObject innerData;
	
	private Message originalResponse;
	private boolean deferred;

	protected Interaction(final BotDiscordClient client, final SjObject data) {
		this.client = Objects.requireNonNull(client);
		id = data.getString("id");
		type = Type.resolve(data.getShort("type"));
		innerData = data.getObject("data");
		token = data.getString("token");
		channelId = data.getObject("channel").getString("id");
		guildId = data.getString("guild_id");
		userId = (guildId == null)
			? data.getObject("user").getString("id")
			: data.getObject("member").getObject("user").getString("id");
	}

	public boolean inGuild() {
		return (guildId != null);
	}

	public CompletableFuture<Guild> getGuildAsync() {
		return client.guilds.get(guildId);
	}

	public CompletableFuture<MessageChannel> getChannelAsync() {
		return client.channels.get(channelId).thenApply(c -> (MessageChannel) c);
	}

	public CompletableFuture<GuildMember> getMemberAsync() {
		return getGuildAsync().thenCompose(g -> g.members.get(userId));
	}

	public CompletableFuture<User> getUserAsync() {
		return client.users.get(userId);
	}
	
	public Message getOriginalResponse() {
		return originalResponse;
	}

	public boolean hasBeenDeferred() {
		return deferred;
	}

	private CompletableFuture<JsonResponse> createResponse(final CallbackType type, final Response payload) {
		final var path = "/interactions/" + id + '/' + token + "/callback";
		final var data = new SjObject();
		data.put("type", type.value);
		if (payload != null)
			data.put("data", payload);
		return client.api.post(path, data.toJsonString());
	}

	public CompletableFuture<Void> deferResponse() {
		return createResponse(CallbackType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE, null)
			.thenRun(() -> deferred = true);
	}

	public CompletableFuture<Void> reply(final Response payload) {
		return createResponse(CallbackType.CHANNEL_MESSAGE_WITH_SOURCE, payload)
			.thenAccept(r -> System.out.println(r.rawText));
	}

	public CompletableFuture<Message> replyThenGetResponse(final Response payload) {
		return createResponse(CallbackType.CHANNEL_MESSAGE_WITH_SOURCE, payload)
			.thenApply(r -> (originalResponse = new Message(client, r.asObject())));
	}

	public CompletableFuture<Message> createFollowupMessage(final Message.Payload payload) {
		final var path = "/webhooks/" + client.application.getId() + '/' + token;
		return client.api.post(path, payload.toJsonString())
			.thenApply(r -> new Message(client, r.asObject()));
	}

	// Content only

	private Response onlyContent(final boolean ephemeral, final String content) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		return payload;
	}

	public CompletableFuture<Void> reply(final String content) {
		return reply(onlyContent(false, content));
	}

	public CompletableFuture<Void> replyEphemeral(final String content) {
		return reply(onlyContent(true, content));
	}

	public CompletableFuture<Message> followUp(final String content) {
		return createFollowupMessage(onlyContent(false, content));
	}

	/**
	 * NOTE: Your first follow up message cannot be ephemeral.
	 */
	public CompletableFuture<Message> followUpEphemeral(final String content) {
		return createFollowupMessage(onlyContent(true, content));
	}

	// Embeds only

	private Response onlyEmbeds(final boolean ephemeral, final Embed... embeds) {
		final var payload = new Response(ephemeral);
		payload.embeds = List.of(embeds);
		return payload;
	}

	public CompletableFuture<Void> reply(final Embed... embeds) {
		return reply(onlyEmbeds(false, embeds));
	}

	public CompletableFuture<Void> replyEphemeral(final Embed... embeds) {
		return reply(onlyEmbeds(true, embeds));
	}

	// Content and embeds

	private Response contentAndEmbeds(final boolean ephemeral, final String content, final Embed... embeds) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		payload.embeds = List.of(embeds);
		return payload;
	}

	public CompletableFuture<Void> reply(final String content, final Embed... embeds) {
		return reply(contentAndEmbeds(false, content, embeds));
	}

	public CompletableFuture<Void> replyEphemeral(final String content, final Embed... embeds) {
		return reply(contentAndEmbeds(true, content, embeds));
	}

	// Content and action rows

	private Response contentAndActionRows(final boolean ephemeral, final String content, final ActionRow... rows) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		payload.components = List.of(rows);
		return payload;
	}

	public CompletableFuture<Void> reply(final String content, final ActionRow... rows) {
		return reply(contentAndActionRows(false, content, rows));
	}

	public CompletableFuture<Void> replyEphemeral(final String content, final ActionRow... rows) {
		return reply(contentAndActionRows(true, content, rows));
	}
}
