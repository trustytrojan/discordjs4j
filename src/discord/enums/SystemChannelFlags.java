package discord.enums;

public enum SystemChannelFlags {
  SuppressJoinNotifications(1),
  SuppressPremiumSubscriptions(2),
  SuppressGuildReminderNotifications(4),
  SuppressJoinNotificationReplies(8);

  public static SystemChannelFlags get(int value) {
    for(final var x : SystemChannelFlags.values())
      if(x.value == value) return x;
    return null;
  }

  private final int value;

  private SystemChannelFlags(int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }
}
