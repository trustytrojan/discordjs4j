package discord.structures.interactions;

import discord.client.BotDiscordClient;
import discord.structures.Guild;
import discord.structures.GuildMember;
import discord.structures.Permissions;
import discord.structures.User;
import discord.structures.channels.TextBasedChannel;

import simple_json.JSONObject;

public abstract class Interaction {
	public static Interaction createCorrectInteraction(BotDiscordClient client, JSONObject data) {
		return switch (Type.resolve(data.getLong("type"))) {
			case APPLICATION_COMMAND -> new ChatInputInteraction(client, data);
			default -> null;
		};
	}

	protected final BotDiscordClient client;

	protected final String id;
	public final Type type;
	public final Guild guild;
	public final GuildMember member;
	public final User user;
	public final TextBasedChannel channel;

	// channelPermissions???
	public final Permissions appPermissions;
	public final Permissions memberPermissions;

	protected final JSONObject innerData;
	protected final String token;

	protected Interaction(final BotDiscordClient client, final JSONObject data) {
		this.client = client;

		id = data.getString("id");
		type = Type.resolve(data.getLong("type"));

		channel = (TextBasedChannel) client.channels.fetch(data.getObject("channel").getString("id")).join();
		innerData = data.getObject("data");
		token = data.getString("token");
		appPermissions = new Permissions(Long.parseLong(data.getString("app_permissions")));
		memberPermissions = new Permissions(Long.parseLong(data.getObject("member").getString("permissions")));

		final var guildId = data.getString("guild_id");
		if (guildId == null) {
			user = client.users.fetch(data.getObject("user").getString("id")).join();
			guild = null;
			member = null;
		} else {
			guild = client.guilds.fetch(guildId).join();
			member = guild.members.fetch(data.getObject("member").getObject("user").getString("id")).join();
			user = member.user;
		}
	}

	public static enum Type {
		PING(1),
		APPLICATION_COMMAND(2),
		MESSAGE_COMPONENT(3),
		APPLICATION_COMMAND_AUTOCOMPLETE(4),
		MODAL_SUBMIT(5);

		public static Type resolve(long value) {
			for (final var x : Type.values())
				if (x.value == value)
					return x;
			return null;
		}

		public final int value;

		private Type(int value) {
			this.value = value;
		}
	}
}
