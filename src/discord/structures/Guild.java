package discord.structures;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
// import discord.enums.DefaultMessageNotificationLevel;
// import discord.enums.ExplicitContentFilterLevel;
// import discord.enums.MFALevel;
// import discord.enums.NSFWLevel;
// import discord.enums.PremiumTier;
// import discord.enums.SystemChannelFlags;
// import discord.enums.VerificationLevel;
import discord.managers.GuildChannelManager;
import discord.structures.channels.TextChannel;

public class Guild implements DiscordObject {

  // Basic metadata
  // public String name;
  // public Long max_stage_video_channel_users;
  // public String preferred_locale;
  // public Boolean large;
  // public Boolean available;
  // public Long premium_subscription_count;
  // public String application_id;
  // public String icon;
  // public String description;
  // public Long afk_timeout;
  // public Long max_members;
  // public Long max_video_channel_users;
  // public Long member_count;
  // public Boolean nsfw;
  // public String vanity_url_code;
  // public Boolean premium_progress_bar_enabled;
  // public String banner;
  // public String discovery_splash;
  // public String splash;
  // public String[] features;

  // // Enumerations
  // public NSFWLevel nsfw_level;
  // public VerificationLevel verification_level;
  // public ExplicitContentFilterLevel explicit_content_filter;
  // public DefaultMessageNotificationLevel default_message_notifications;
  // public MFALevel mfa_level;
  // public PremiumTier premium_tier;
  // public SystemChannelFlags system_channel_flags;

  // // Special channels
  // public String system_channel_id;
  // //public TextChannel system_channel;
  // public String rules_channel_id;
  // //public TextChannel rules_channel;
  // public String afk_channel_id;
  // //public VoiceChannel afk_channel;
  // public String safety_alerts_channel_id;
  // //public TextChannel safety_alerts_channel;
  // public String public_updates_channel_id;
  // //public TextChannel public_updates_channel_id;

  // Large amounts of data
  //public RoleManager roles;
  //public VoiceState[] voice_states;
  //public Sticker[] stickers;
  //public Object[] stage_instances;
  //public GuildScheduledEvent[] events;
  //public GuildEmojiManager emojis;
  //presences... these will be applied to GuildMember objects
  //threads... figure this out later

  // Unknown metadata
  //public Object[] embedded_activities;
  //public Object hub_type;
  //joined_at... this can go somewhere else
  //public Object home_header; probably the boost progress bar
  //public boolean lazy; ????

  private final DiscordClient client;
  private BetterJSONObject data;

  public final GuildChannelManager channels;
  //public final MemberManager members;
  //public final RoleManager roles;
  
  public Guild(DiscordClient client, BetterJSONObject data) {
    this.client = client;
    this.data = data;
    channels = new GuildChannelManager(client, this);
  }

  public String name() {
    return data.getString("name");
  }

  public TextChannel system_channel() throws Exception {
    final var id = data.getString("system_channel_id");
    return (TextChannel)client.channels.fetch(id).get();
  }

  @Override
  public BetterJSONObject getData() {
    return data;
  }

  @Override
  public void setData(BetterJSONObject data) {
    this.data = data;
  }

  @Override
  public DiscordClient client() {
    return client;
  }

  @Override
  public String api_path() {
    return String.format("/guilds/%s", id());
  }

}
