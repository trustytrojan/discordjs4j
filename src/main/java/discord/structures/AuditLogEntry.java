package discord.structures;

import java.util.HashMap;
import java.util.Map;

import discord.client.DiscordClient;
import discord.enums.AuditLogEvent;
import simple_json.SjObject;

public class AuditLogEntry {
	public final String id;
	public final User executor;
	public final Guild guild;
	public final AuditLogEvent actionType;
	public final String targetId;
	public final String reason;
	public final Map<String, AuditLogChange> changes = new HashMap<>();

	public AuditLogEntry(DiscordClient client, SjObject data) {
		id = data.getString("id");
		guild = client.guilds.fetch(data.getString("guild_id")).join();
		executor = client.users.fetch(data.getString("user_id")).join();
		targetId = data.getString("target_id");
		reason = data.getString("reason");
		actionType = AuditLogEvent.resolve(data.getLong("action_type"));

		for (final var changeData : data.getObjectArray("changes")) {
			final var change = new AuditLogChange(changeData);
			changes.put(change.key, change);
		}
	}

	public static class AuditLogChange {
		public final String key;
		public final Object oldValue;
		public final Object newValue;

		private AuditLogChange(SjObject data) {
			key = data.getString("key");
			oldValue = data.get("old_value");
			newValue = data.get("new_value");
		}
	}
}