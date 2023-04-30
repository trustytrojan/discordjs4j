package discord.structures.interactions;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.structures.Embed;
import discord.structures.Guild;
import discord.structures.GuildMember;
import discord.structures.Message;
import discord.structures.Permissions;
import discord.structures.User;
import discord.structures.channels.TextBasedChannel;
import discord.structures.components.ActionRow;
import discord.util.Util;
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

	public static class Reply extends Message.Payload {
		private final boolean ephemeral;

		public Reply(final boolean ephemeral) {
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
	protected final String token;

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

	private CompletableFuture<Void> sendResponse(final CallbackType type, final Reply payload) {
		final var path = "/interactions/" + id + '/' + token + "/callback";
		final var data = new JSONObject();
		data.put("type", type.value);
		if (payload != null)
			data.put("data", payload);
		return client.api.post(path, data.toJSONString()).thenRunAsync(Util.DO_NOTHING);
	}

	public CompletableFuture<Void> deferReply() {
		return sendResponse(CallbackType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE, null);
	}

	public CompletableFuture<Void> reply(final Reply payload) {
		return sendResponse(CallbackType.CHANNEL_MESSAGE_WITH_SOURCE, payload);
	}

	// Content only

	private Reply onlyContent(final boolean ephemeral, final String content) {
		final var payload = new Reply(ephemeral);
		payload.content = content;
		return payload;
	}

	public CompletableFuture<Void> reply(final String content) {
		return reply(onlyContent(false, content));
	}

	public CompletableFuture<Void> replyEphemeral(final String content) {
		return reply(onlyContent(true, content));
	}

	// Embeds only

	private Reply onlyEmbeds(final boolean ephemeral, final Embed... embeds) {
		final var payload = new Reply(ephemeral);
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

	private Reply contentAndEmbeds(final boolean ephemeral, final String content, final Embed... embeds) {
		final var payload = new Reply(ephemeral);
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

	private Reply contentAndActionRows(final boolean ephemeral, final String content, final ActionRow... rows) {
		final var payload = new Reply(ephemeral);
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
