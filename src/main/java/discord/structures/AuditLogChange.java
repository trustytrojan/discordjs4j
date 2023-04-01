package discord.structures;

import simple_json.JSONObject;

public class AuditLogChange {
	
	public final String key;
	public final Object oldValue;
	public final Object newValue;

	AuditLogChange(JSONObject data) {
		key = data.getString("key");
		oldValue = data.get("old_value");
		newValue = data.get("new_value");
	}

}
