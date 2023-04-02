package discord.structures;

import simple_json.JSONObject;

import java.util.HashMap;

import discord.client.DiscordClient;
import discord.enums.AuditLogEvent;

public class AuditLogEntry implements DiscordResource {

	private final DiscordClient client;
	private final JSONObject data;

	public final User executor;
	public final Guild guild;
	public final HashMap<String, AuditLogChange> changes = new HashMap<>();

	public AuditLogEntry(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;

		guild = client.guilds.fetch(guildId());
		executor = client.users.fetch(executorId());

		for (final var changeData : data.getObjectArray("changes")) {
			final var change = new AuditLogChange(changeData);
			changes.put(change.key, change);
		}
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

	public DiscordClient client() {
		return client;
	}

	public JSONObject getData() {
		return data;
	}

}
