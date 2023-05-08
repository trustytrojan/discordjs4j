package discord.structures.interactions;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.client.APIClient.JSONHttpResponse;
import discord.structures.Embed;
import discord.structures.Guild;
import discord.structures.GuildMember;
import discord.structures.Message;
import discord.structures.Permissions;
import discord.structures.User;
import discord.structures.channels.TextBasedChannel;
import discord.structures.components.ActionRow;
import simple_json.JSONObject;

public abstract class Interaction {
	public static enum Type {
		PING(1),
		APPLICATION_COMMAND(2),
		MESSAGE_COMPONENT(3),
		APPLICATION_COMMAND_AUTOCOMPLETE(4),
		MODAL_SUBMIT(5);

		public static Type resolve(final short value) {
			for (final var x : Type.values())
				if (x.value == value)
					return x;
			return null;
		}

		public final short value;

		private Type(final int value) {
			this.value = (short) value;
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

		private CallbackType(int value) {
			this.value = value;
		}
	}

	public static class Response extends Message.Payload {
		private final boolean ephemeral;

		public Response(final boolean ephemeral) {
			this.ephemeral = ephemeral;
		}

		@Override
		public String toJSONString() {
			final var obj = toJSONObject();
			if (ephemeral)
				obj.put("ephemeral", Boolean.TRUE);
			return obj.toString();
		}
	}

	public static Interaction fromJSON(final BotDiscordClient client, final JSONObject data) {
		return switch (Type.resolve(data.getShort("type"))) {
			case APPLICATION_COMMAND -> new ChatInputInteraction(client, data);
			case MESSAGE_COMPONENT -> new MessageComponentInteraction(client, data);
			default -> null;
		};
	}

	protected final BotDiscordClient client;
	private final String id;

	public final Type type;
	public final Guild guild;
	public final GuildMember member;
	public final User user;
	public final TextBasedChannel channel;
	public final Permissions appPermissions;
	public final Permissions memberPermissions;

	protected final JSONObject innerData;
	private final String token;

	private Message originalResponse;
	private boolean deferred;

	protected Interaction(final BotDiscordClient client, final JSONObject data) {
		this.client = Objects.requireNonNull(client);

		id = data.getString("id");
		type = Type.resolve(data.getShort("type"));

		channel = (TextBasedChannel) client.channels.fetch(data.getObject("channel").getString("id")).join();
		innerData = data.getObject("data");
		token = data.getString("token");

		final var guildId = data.getString("guild_id");
		if (guildId == null) {
			user = client.users.fetch(data.getObject("user").getString("id")).join();
			guild = null;
			member = null;
			appPermissions = null;
			memberPermissions = null;
		} else {
			appPermissions = new Permissions(Long.parseLong(data.getString("app_permissions")));
			memberPermissions = new Permissions(Long.parseLong(data.getObject("member").getString("permissions")));
			guild = client.guilds.fetch(guildId).join();
			member = guild.members.fetch(data.getObject("member").getObject("user").getString("id")).join();
			user = member.user;
		}
	}

	public boolean inGuild() {
		return (guild != null);
	}

	public Message getOriginalResponse() {
		return originalResponse;
	}

	public boolean hasBeenDeferred() {
		return deferred;
	}

	private CompletableFuture<JSONHttpResponse> createResponse(final CallbackType type, final Response payload) {
		final var path = "/interactions/" + id + '/' + token + "/callback";
		final var data = new JSONObject();
		data.put("type", type.value);
		if (payload != null)
			data.put("data", payload);
		return client.api.post(path, data.toJSONString());
	}

	public CompletableFuture<Void> deferResponse() {
		return createResponse(CallbackType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE, null)
			.thenRunAsync(() -> deferred = true);
	}

	public CompletableFuture<Void> respond(final Response payload) {
		return createResponse(CallbackType.CHANNEL_MESSAGE_WITH_SOURCE, payload)
			.thenAcceptAsync(r -> System.out.println(r.body));
	}

	public CompletableFuture<Message> respondThenGetResponse(final Response payload) {
		return createResponse(CallbackType.CHANNEL_MESSAGE_WITH_SOURCE, payload)
			.thenApplyAsync(
				(final var r) -> (originalResponse = new Message(client, r.toJSONObject()))
			);
	}

	public CompletableFuture<Message> createFollowupMessage(final Message.Payload payload) {
		final var path = "/webhooks/" + client.application.id() + '/' + token;
		return client.api.post(path, payload.toJSONString())
			.thenApplyAsync((final var r) -> {
				return new Message(client, r.toJSONObject());
			});
	}

	// Content only

	private Response onlyContent(final boolean ephemeral, final String content) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		return payload;
	}

	public CompletableFuture<Void> respond(final String content) {
		return respond(onlyContent(false, content));
	}

	public CompletableFuture<Void> respondEphemeral(final String content) {
		return respond(onlyContent(true, content));
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

	public CompletableFuture<Void> respond(final Embed... embeds) {
		return respond(onlyEmbeds(false, embeds));
	}

	public CompletableFuture<Void> respondEphemeral(final Embed... embeds) {
		return respond(onlyEmbeds(true, embeds));
	}

	// Content and embeds

	private Response contentAndEmbeds(final boolean ephemeral, final String content, final Embed... embeds) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		payload.embeds = List.of(embeds);
		return payload;
	}

	public CompletableFuture<Void> respond(final String content, final Embed... embeds) {
		return respond(contentAndEmbeds(false, content, embeds));
	}

	public CompletableFuture<Void> respondEphemeral(final String content, final Embed... embeds) {
		return respond(contentAndEmbeds(true, content, embeds));
	}

	// Content and action rows

	private Response contentAndActionRows(final boolean ephemeral, final String content, final ActionRow... rows) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		payload.components = List.of(rows);
		return payload;
	}

	public CompletableFuture<Void> respond(final String content, final ActionRow... rows) {
		return respond(contentAndActionRows(false, content, rows));
	}

	public CompletableFuture<Void> respondEphemeral(final String content, final ActionRow... rows) {
		return respond(contentAndActionRows(true, content, rows));
	}
}
