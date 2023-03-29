package discord.structures.interactions;

import discord.client.DiscordClient;
import discord.enums.InteractionType;
import discord.structures.Guild;
import discord.structures.User;
import discord.structures.channels.TextBasedChannel;
import simple_json.JSONObject;

public class Interaction {

	public static Interaction createCorrectInteraction(DiscordClient client, JSONObject data) {
		return switch (InteractionType.resolve(data.getInt("type"))) {
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
		String user_id;
		if (inGuild()) {
			user_id = data.getObject("member").getObject("user").getString("id");
			guild = client.guilds.fetch(guildId());
		} else {
			user_id = data.getObject("user").getString("id");
			guild = null;
		}
		user = client.users.fetch(user_id);
		channel = (TextBasedChannel) client.channels.fetch(channelId());
	}

	protected JSONObject innerData() {
		return data.getObject("data");
	}

	public String id() {
		return data.getString("id");
	}

	public InteractionType type() {
		return InteractionType.resolve(data.getInt("type"));
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
