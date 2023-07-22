package discord.resources;

import discord.client.DiscordClient;
import discord.resources.guilds.Guild;
import sj.SjObject;

/**
 * Keeps a constant reference to the resource's guild, since guild resources only
 * exist on one guild.
 */
public class AbstractGuildResource extends AbstractDiscordResource implements GuildResource {
	protected final String guildId;
	protected final Guild guild;

	protected AbstractGuildResource(DiscordClient client, SjObject data, String guildApiPath) {
		super(client, data, "/guilds/" + guildApiPath);
		guildId = data.getString("guild_id");
		guild = client.guilds.get(guildId).join();
	}

	@Override
	public Guild guild() {
		return guild;
	}

	@Override
	public String guildId() {
		return guildId;
	}
}
