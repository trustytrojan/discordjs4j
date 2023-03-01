package discord.structures;

public interface GuildObject extends DiscordObject {

	default String guildId() {
		return getData().getString("guild_id");
	}

	public Guild guild();
	
}
