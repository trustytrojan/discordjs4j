package discord.structures.interactions;

import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.enums.InteractionCallbackType;
import discord.structures.Embed;
import discord.structures.Guild;
import discord.structures.GuildMember;
import discord.structures.InteractionReplyMessagePayload;
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

	protected final BotDiscordClient client;

	protected final String id;
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
		this.client = client;

		id = data.getString("id");
		type = Type.resolve(data.getShort("type"));

		channel = (TextBasedChannel) client.channels.fetch(data.getObject("channel").getString("id")).join();
		innerData = data.getObject("data");
		System.out.println(innerData);
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

	private CompletableFuture<Void> _reply(final InteractionCallbackType type, final InteractionReplyMessagePayload payload) {
		final var path = "/interactions/" + id + '/' + token + "/callback";
		final var data = new JSONObject();
		data.put("type", type.value);
		data.put("data", payload);
		System.out.println("here");
		return client.api.post(path, data.toJSONString()).thenRunAsync(Util.DO_NOTHING);
	}

	public CompletableFuture<Void> deferReply() {
		return _reply(InteractionCallbackType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE, null);
	}

	public CompletableFuture<Void> reply(final String content) {
		final var payload = new InteractionReplyMessagePayload();
		payload.content = content;
		return reply(payload);
	}

	public CompletableFuture<Void> replyEphemeral(final String content) {
		final var payload = new InteractionReplyMessagePayload();
		payload.content = content;
		payload.ephemeral = Boolean.TRUE;
		return reply(payload);
	}

	public CompletableFuture<Void> reply(final Embed... embeds) {
		final var payload = new InteractionReplyMessagePayload();
		payload.addEmbeds(embeds);
		return reply(payload);
	}

	public CompletableFuture<Void> replyEphemeral(final Embed... embeds) {
		final var payload = new InteractionReplyMessagePayload();
		payload.addEmbeds(embeds);
		payload.ephemeral = Boolean.TRUE;
		return reply(payload);
	}

	public CompletableFuture<Void> reply(final String content, final ActionRow... components) {
		final var payload = new InteractionReplyMessagePayload();
		payload.content = content;
		payload.addComponents(components);
		return reply(payload);
	}

	public CompletableFuture<Void> replyEphemeral(final String content, final ActionRow... components) {
		final var payload = new InteractionReplyMessagePayload();
		payload.content = content;
		payload.addComponents(components);
		payload.ephemeral = Boolean.TRUE;
		return reply(payload);
	}

	public CompletableFuture<Void> reply(final InteractionReplyMessagePayload payload) {
		return _reply(InteractionCallbackType.CHANNEL_MESSAGE_WITH_SOURCE, payload);
	}
}
