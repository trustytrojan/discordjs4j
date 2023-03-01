package discord.structures;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.util.BetterMap;
import discord.client.DiscordClient;
import discord.enums.AuditLogEvent;

public class AuditLogEntry {

	private final BetterJSONObject data;
	private User executor;
	private final CompletableFuture<Void> _executor;
	private Guild guild;
	private final CompletableFuture<Void> _guild;
	public final BetterMap<String, AuditLogChange> changes = new BetterMap<>();

	public AuditLogEntry(DiscordClient client, BetterJSONObject data) {
		System.out.println(data);
		this.data = data;
		_guild = client.guilds.fetch(guildId()).thenAccept((guild) -> this.guild = guild);
		_executor = client.users.fetch(executorId()).thenAccept((user) -> executor = user);
		for (final var change_data : data.getObjectArray("changes")) {
			changes.put(change_data.getString("key"), new AuditLogChange(change_data));
		}
	}

	public String id() {
		return data.getString("id");
	}

	public String guildId() {
		return data.getString("guild_id");
	}

	public Guild guild() {
		if (guild == null) {
			try {
				_guild.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return guild;
	}

	public String executorId() {
		return data.getString("user_id");
	}

	public User executor() {
		if (executor == null) {
			try {
				_executor.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return executor;
	}

	public String targetId() {
		return data.getString("target_id");
	}

	public String reason() {
		return data.getString("reason");
	}

	public AuditLogEvent actionType() {
		return AuditLogEvent.resolve(data.getLong("action_type"));
	}

}
