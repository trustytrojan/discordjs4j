package discord.structures.interactions;

import discord.client.DiscordClient;
import discord.enums.InteractionType;
import discord.structures.Guild;
import discord.structures.User;
import discord.structures.channels.TextBasedChannel;
import simple_json.JSONObject;

public class Interaction {

	public static Interaction createCorrectInteraction(DiscordClient client, JSONObject data) {
		return switch (InteractionType.resolve(data.getLong("type").intValue())) {
			case ApplicationCommand -> new ChatInputInteraction(client, data);
			default -> null;
		};
	}

	protected final DiscordClient client;
	private final JSONObject data;

	public final User user;
	public final TextBasedChannel channel;
	public final Guild guild;

	protected Interaction(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;

		String userId;
		if (inGuild()) {
			userId = data.getObject("member").getObject("user").getString("id");
			guild = client.guilds.fetch(guildId());
		} else {
			userId = data.getObject("user").getString("id");
			guild = null;
		}

		user = client.users.fetch(userId);
		channel = (TextBasedChannel) client.channels.fetch(channelId());
	}

	protected JSONObject innerData() {
		return data.getObject("data");
	}

	public String id() {
		return data.getString("id");
	}

	public InteractionType type() {
		return InteractionType.resolve(data.getLong("type").intValue());
	}

	public String applicationId() {
		return data.getString("application_id");
	}

	public boolean inGuild() {
		return (guildId() != null);
	}

	public String channelId() {
		return data.getString("channel_id");
	}

	public String guildId() {
		return data.getString("guild_id");
	}

	public String token() {
		return data.getString("token");
	}

}
