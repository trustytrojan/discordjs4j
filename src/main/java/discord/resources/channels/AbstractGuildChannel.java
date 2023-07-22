package discord.resources.channels;

import discord.client.DiscordClient;
import discord.resources.AbstractGuildResource;
import sj.SjObject;

public abstract class AbstractGuildChannel extends AbstractGuildResource implements GuildChannel {
	private final String url = "https://discord.com/channels/" + guild.id() + '/' + id;

	protected AbstractGuildChannel(DiscordClient client, SjObject data) {
		super(client, client.guilds.get(data.getString("guild_id")).join(), data, "/channels");
	}

	@Override
	public String url() {
		return url;
	}
}
