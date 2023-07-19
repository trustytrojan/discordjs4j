package discord.resources.interactions;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.APIClient.JsonResponse;
import discord.client.BotDiscordClient;
import discord.enums.Permission;
import discord.resources.Embed;
import discord.resources.GuildMember;
import discord.resources.Message;
import discord.resources.User;
import discord.resources.channels.MessageChannel;
import discord.resources.components.ActionRow;
import discord.resources.guilds.Guild;
import discord.util.BitFlagSet;
import sj.SjObject;

public abstract class Interaction {
	public static enum Type {
		PING(1),
		APPLICATION_COMMAND(2),
		MESSAGE_COMPONENT(3),
		APPLICATION_COMMAND_AUTOCOMPLETE(4),
		MODAL_SUBMIT(5);

		public static Type resolve(short value) {
			for (final var x : Type.values())
				if (x.value == value)
					return x;
			return null;
		}

		public final short value;

		private Type(int value) {
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

		public Response(boolean ephemeral) {
			this.ephemeral = ephemeral;
		}

		@Override
		public String toJsonString() {
			final var obj = toJSONObject();
			if (ephemeral)
				obj.put("ephemeral", Boolean.TRUE);
			return obj.toString();
		}
	}

	public static Interaction fromJSON(BotDiscordClient client, SjObject data) {
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
	public final MessageChannel channel;
	public final BitFlagSet<Permission> appPermissions;
	public final BitFlagSet<Permission> memberPermissions;

	protected final SjObject innerData;
	private final String token;

	private Message originalResponse;
	private boolean deferred;

	protected Interaction(BotDiscordClient client, SjObject data) {
		this.client = Objects.requireNonNull(client);

		id = data.getString("id");
		type = Type.resolve(data.getShort("type"));

		channel = (MessageChannel) client.channels.get(data.getObject("channel").getString("id")).join();
		innerData = data.getObject("data");
		token = data.getString("token");

		final var guildId = data.getString("guild_id");
		if (guildId == null) {
			user = client.users.get(data.getObject("user").getString("id")).join();
			guild = null;
			member = null;
			appPermissions = null;
			memberPermissions = null;
		} else {
			appPermissions = new BitFlagSet<>(Long.parseLong(data.getString("app_permissions")));
			memberPermissions = new BitFlagSet<>(Long.parseLong(data.getObject("member").getString("permissions")));
			guild = client.guilds.get(guildId).join();
			member = guild.members.get(data.getObject("member").getObject("user").getString("id")).join();
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

	private CompletableFuture<JsonResponse> createResponse(CallbackType type, Response payload) {
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

	public CompletableFuture<Void> respond(Response payload) {
		return createResponse(CallbackType.CHANNEL_MESSAGE_WITH_SOURCE, payload)
			.thenAccept(r -> System.out.println(r.text));
	}

	public CompletableFuture<Message> respondThenGetResponse(Response payload) {
		return createResponse(CallbackType.CHANNEL_MESSAGE_WITH_SOURCE, payload)
			.thenApply(r -> (originalResponse = new Message(client, channel, r.toJsonObject())));
	}

	public CompletableFuture<Message> createFollowupMessage(Message.Payload payload) {
		final var path = "/webhooks/" + client.application.id() + '/' + token;
		return client.api.post(path, payload.toJsonString())
			.thenApply(r -> new Message(client, channel, r.toJsonObject()));
	}

	// Content only

	private Response onlyContent(boolean ephemeral, String content) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		return payload;
	}

	public CompletableFuture<Void> respond(String content) {
		return respond(onlyContent(false, content));
	}

	public CompletableFuture<Void> respondEphemeral(String content) {
		return respond(onlyContent(true, content));
	}

	public CompletableFuture<Message> followUp(String content) {
		return createFollowupMessage(onlyContent(false, content));
	}

	/**
	 * NOTE: Your first follow up message cannot be ephemeral.
	 */
	public CompletableFuture<Message> followUpEphemeral(String content) {
		return createFollowupMessage(onlyContent(true, content));
	}

	// Embeds only

	private Response onlyEmbeds(boolean ephemeral, Embed... embeds) {
		final var payload = new Response(ephemeral);
		payload.embeds = List.of(embeds);
		return payload;
	}

	public CompletableFuture<Void> respond(Embed... embeds) {
		return respond(onlyEmbeds(false, embeds));
	}

	public CompletableFuture<Void> respondEphemeral(Embed... embeds) {
		return respond(onlyEmbeds(true, embeds));
	}

	// Content and embeds

	private Response contentAndEmbeds(boolean ephemeral, String content, Embed... embeds) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		payload.embeds = List.of(embeds);
		return payload;
	}

	public CompletableFuture<Void> respond(String content, Embed... embeds) {
		return respond(contentAndEmbeds(false, content, embeds));
	}

	public CompletableFuture<Void> respondEphemeral(String content, Embed... embeds) {
		return respond(contentAndEmbeds(true, content, embeds));
	}

	// Content and action rows

	private Response contentAndActionRows(boolean ephemeral, String content, ActionRow... rows) {
		final var payload = new Response(ephemeral);
		payload.content = content;
		payload.components = List.of(rows);
		return payload;
	}

	public CompletableFuture<Void> respond(String content, ActionRow... rows) {
		return respond(contentAndActionRows(false, content, rows));
	}

	public CompletableFuture<Void> respondEphemeral(String content, ActionRow... rows) {
		return respond(contentAndActionRows(true, content, rows));
	}
}