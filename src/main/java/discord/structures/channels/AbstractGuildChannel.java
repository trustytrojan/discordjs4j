package discord.structures.channels;

import discord.client.DiscordClient;
import discord.structures.AbstractGuildResource;
import sj.SjObject;

public abstract class AbstractGuildChannel extends AbstractGuildResource implements GuildChannel {
	private final String url = "https://discord.com/channels/" + guild.id + '/' + id;

	protected AbstractGuildChannel(DiscordClient client, SjObject data) {
		super(client, data);
	}

	@Override
	public String url() {
		return url;
	}
}
