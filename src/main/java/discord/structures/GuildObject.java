package discord.structures;

public interface GuildObject extends DiscordResource {
    
    default String guildId() {
        return getData().getString("guild_id");
    }

}
