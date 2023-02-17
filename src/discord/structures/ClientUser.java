package discord.structures;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;

public class ClientUser extends User {

  public ClientUser(DiscordClient client, BetterJSONObject data) {
    super(client, data);
  }

  

}
