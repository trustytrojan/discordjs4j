package discord.structures;

import discord.util.BetterJSONObject;

public class AuditLogChange {
	
	private final BetterJSONObject data;

	AuditLogChange(BetterJSONObject data) {
		this.data = data;
	}

	public String key() {
		return data.getString("key");
	}

	public Object oldValue() {
		return data.get("old_value");
	}

	public Object newValue() {
		return data.get("new_value");
	}

}
