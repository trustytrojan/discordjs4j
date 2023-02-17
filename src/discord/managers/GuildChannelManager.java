package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.channels.GuildChannel;

public class GuildChannelManager extends DataManager<GuildChannel> {
  
  public final Guild guild;

  public GuildChannelManager(DiscordClient client, Guild guild) {
    super(client);
    this.guild = guild;
  }

  @Override
  public GuildChannel forceCache(BetterJSONObject data) {
    return cache((GuildChannel)client.channels.createCorrectChannel(data));
  }

  @Override
  public CompletableFuture<GuildChannel> fetch(String id, boolean force) {
    final var path = String.format("/channels/%s", id);
    return super.fetch(id, path, force);
  }
  
}
