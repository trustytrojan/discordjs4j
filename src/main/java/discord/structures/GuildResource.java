package discord.structures;

public interface GuildResource extends DiscordResource {

    Guild guild();
    
    default String guildId() {
        return getData().getString("guild_id");
    }

}
