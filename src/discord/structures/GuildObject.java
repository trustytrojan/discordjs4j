package discord.structures;

public interface GuildObject extends DiscordObject {

	default String guild_id() {
		return getData().getString("guild_id");
	}

	public Guild guild();
	
}
