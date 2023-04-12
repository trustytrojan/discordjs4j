package discord.structures.interactions;

import discord.client.BotDiscordClient;
import discord.structures.Guild;
import discord.structures.GuildMember;
import discord.structures.User;
import discord.structures.channels.TextBasedChannel;

import simple_json.JSONObject;

public abstract class Interaction {

	public static Interaction createCorrectInteraction(BotDiscordClient client, JSONObject data) {
		return switch (Type.resolve(data.getLong("type").intValue())) {
			case APPLICATION_COMMAND -> new ChatInputInteraction(client, data);
			default -> null;
		};
	}

	protected final BotDiscordClient client;

	public final String id;
	public final Type type;
	public final Guild guild;
	public final GuildMember member;
	public final User user;
	public final TextBasedChannel channel;
	
	protected final JSONObject innerData;
	protected final String token;

	protected Interaction(BotDiscordClient client, JSONObject data) {
		this.client = client;

		id = data.getString("id");
		type = Type.resolve(data.getLong("type"));
		
		channel = (TextBasedChannel) client.channels.fetch(data.getObject("channel").getString("id"));
		innerData = data.getObject("data");
		token = data.getString("token");

		final var guildId = data.getString("guild_id");
		if (guildId == null) {
			user = client.users.fetch(data.getObject("user").getString("id"));
			guild = null;
			member = null;
		} else {
			guild = client.guilds.fetch(data.getString("guild_id"));
			member = new GuildMember(client, guild, data.getObject("member"));
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
