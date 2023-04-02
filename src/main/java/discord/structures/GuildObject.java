package discord.structures;

public interface GuildObject extends DiscordResource {

    Guild guild();
    
    default String guildId() {
        return getData().getString("guild_id");
    }

}
