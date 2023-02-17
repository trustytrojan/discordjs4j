package discord.enums;

public enum CommandOptionType {

  Subcommand(1),
  SubcommandGroup(2),
  String(3),
  Integer(4),
  Boolean(5),
  User(6),
  Channel(7),
  Role(8),
  Mentionable(9),
  Number(10),
  Attachment(11);

  public static CommandOptionType get(long value) {
    for(final var x : CommandOptionType.values())
      if(x.value == value) return x;
    return null;
  }

  private final int value;

  private CommandOptionType(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }

}
