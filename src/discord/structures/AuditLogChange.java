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

  public Object old_value() {
    return data.get("old_value");
  }

  public Object new_value() {
    return data.get("new_value");
  }

}
