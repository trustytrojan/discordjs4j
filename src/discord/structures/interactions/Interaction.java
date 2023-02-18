package discord.structures.interactions;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.enums.InteractionType;
import discord.structures.Guild;
import discord.structures.User;
import discord.structures.channels.TextBasedChannel;

public abstract class Interaction {

	public static Interaction createCorrectInteraction(DiscordClient client, BetterJSONObject data) {
		return switch(InteractionType.get(data.getLong("type"))) {
			case ApplicationCommand -> new ChatInputInteraction(client, data);
			default -> null;
		};
	}
	
	protected final DiscordClient client;
	protected BetterJSONObject data;

	private User user;
	private final CompletableFuture<Void> _user;
	private TextBasedChannel channel;
	private final CompletableFuture<Void> _channel;
	private Guild guild;
	private final CompletableFuture<Void> _guild;

	protected Interaction(DiscordClient client, BetterJSONObject data) {
		this.client = client;
		this.data = data;

		String user_id;
		if(inGuild()) {
			user_id = data.getObject("member").getObject("user").getString("id");
			_guild = client.guilds.fetch(guildId()).thenAccept((guild) -> this.guild = guild);
		}
		else {
			user_id = data.getObject("user").getString("id");
			_guild = null;
		}
		_user = client.users.fetch(user_id).thenAccept((user) -> this.user = user);

		_channel = client.channels.fetch(channelId()).thenAccept((channel) -> this.channel = (TextBasedChannel)channel);
	}

	protected BetterJSONObject innerData() {
		return data.getObject("data");
	}

	public String id() {
		return data.getString("id");
	}

	public InteractionType type() {
		return InteractionType.get(data.getLong("type"));
	}

	public String applicationId() {
		return data.getString("application_id");
	}

	public boolean inGuild() {
		return (guildId() != null);
	}

	public User user() {
		if(user == null)
			try { _user.get(); }
			catch(Exception e) { throw new RuntimeException(e); }
		return user;
	}

	public String channelId() {
		return data.getString("channel_id");
	}

	public TextBasedChannel channel() {
		if(channel == null)
			try { _channel.get(); }
			catch(Exception e) { throw new RuntimeException(e); }
		return channel;
	}

	public String guildId() {
		return data.getString("guild_id");
	}

	public Guild guild() {
		if(guild == null)
			try { _guild.get(); }
			catch(Exception e) { throw new RuntimeException(e); }
		return guild;
	}

	public String token() {
		return data.getString("token");
	}

}
