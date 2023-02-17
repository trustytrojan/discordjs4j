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
  public final BetterMap<String, AuditLogChange> changes = new BetterMap<>();

  public AuditLogEntry(DiscordClient client, BetterJSONObject data) {
    this.data = data;
    _executor = client.users.fetch(user_id()).thenAccept((user) -> executor = user);
    
    for(final var change_data : data.getObjectArray("changes")) {
      changes.put(change_data.getString("key"), new AuditLogChange(change_data));
    }
  }

  public String id() {
    return data.getString("id");
  }

  public String user_id() {
    return data.getString("user_id");
  }

  public User executor() {
    if(executor == null)
      try { _executor.get(); }
      catch(Exception e) { throw new RuntimeException(e); }
    return executor;
  }

  public String target_id() {
    return data.getString("target_id");
  }

  public String reason() {
    return data.getString("reason");
  }

  public AuditLogEvent action_type() {
    return AuditLogEvent.get(data.getLong("action_type"));
  }

}
