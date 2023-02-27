package discord.structures;

import discord.util.BetterJSONObject;

public class AuditLogChange {
	
	public final String key;
	public final Object oldValue;
	public final Object newValue;

	AuditLogChange(BetterJSONObject data) {
		key = data.getString("key");
		oldValue = data.get("old_value");
		newValue = data.get("new_value");
	}

}
