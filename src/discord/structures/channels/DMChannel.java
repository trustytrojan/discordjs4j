package discord.structures.channels;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.enums.ChannelType;
import discord.managers.MessageManager;
import discord.structures.User;

public class DMChannel implements TextBasedChannel {

  private final DiscordClient client;
  private BetterJSONObject data;
  private final MessageManager messages;
  public final User recipient;

  public DMChannel(DiscordClient client, BetterJSONObject data) {
    this.client = client;
    this.data = data;
    messages = new MessageManager(client, this);
    this.recipient = client.users.cache(data.getObjectArray("recipients").get(0));
  }

  @Override
  public MessageManager messages() {
    return messages;
  }

  @Override
  public ChannelType type() {
    return ChannelType.get(data.getLong("type"));
  }

  @Override
  public String name() {
    return String.format("DM with %s", recipient.tag());
  }

  public String last_message_id() {
    return data.getString("last_message_id");
  }

  @Override
  public void setData(BetterJSONObject data) {
    this.data = data;
  }

  @Override
  public BetterJSONObject getData() {
    return data;
  }

  @Override
  public DiscordClient client() {
    return client;
  }

}
