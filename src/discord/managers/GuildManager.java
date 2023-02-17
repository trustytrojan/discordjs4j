package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.util.BetterMap;
import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.util.JSON;

public class GuildManager extends DataManager<Guild> {

  public GuildManager(DiscordClient client) {
    super(client);
  }

  @Override
  public Guild forceCache(BetterJSONObject data) {
    return cache(new Guild(client, data));
  }

  @Override
  public CompletableFuture<Guild> fetch(String id, boolean force) {
    final var path = String.format("/guilds/%s", id);
    return super.fetch(id, path, force);
  }

  public CompletableFuture<BetterMap<String, Guild>> fetch() {
    return CompletableFuture.supplyAsync(() -> {
      try {
        final var partials = JSON.parseObjectArray(client.api.get("/users/@me/guilds"));
        final var guilds = new BetterMap<String, Guild>();
        for(final var partial : partials) {
          cache(partial);
        }
        return guilds;
      } catch(Exception e) {
        e.printStackTrace();
        return null;
      }
    });
  }

}
