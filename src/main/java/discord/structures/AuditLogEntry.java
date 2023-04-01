package discord.structures;

import discord.util.BetterMap;
import simple_json.JSONObject;
import discord.client.DiscordClient;
import discord.enums.AuditLogEvent;

public class AuditLogEntry {

	private final JSONObject data;
	public final User executor;
	public final Guild guild;
	public final BetterMap<String, AuditLogChange> changes = new BetterMap<>();

	public AuditLogEntry(DiscordClient client, JSONObject data) {
		this.data = data;
		guild = client.guilds.fetch(guildId());
		executor = client.users.fetch(executorId());
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

	public String executorId() {
		return data.getString("user_id");
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
