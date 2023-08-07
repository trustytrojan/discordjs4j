package discord.resources;

import discord.client.DiscordClient;
import discord.resources.guilds.Guild;
import sj.SjObject;

/**
 * Keeps a constant reference to the resource's guild, since guild resources only
 * exist on one guild.
 */
public class AbstractGuildResource extends AbstractDiscordResource implements GuildResource {
	public final Guild guild;

	protected AbstractGuildResource(DiscordClient client, Guild guild, SjObject data) {
		super(client, data);
		this.guild = guild;
	}

	protected AbstractGuildResource(DiscordClient client, Guild guild, SjObject data, String id) {
		super(client, data, id);
		this.guild = guild;
	}

	@Override
	public Guild getGuild() {
		return guild;
	}
}
