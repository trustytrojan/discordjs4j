package discord.resources.channels;

import discord.client.DiscordClient;
import discord.resources.AbstractDiscordResource;
import sj.SjObject;

public class DMBasedChannel extends AbstractDiscordResource implements Channel {
	private final String url = "https://discord.com/channels/@me/" + id;

	protected DMBasedChannel(DiscordClient client, SjObject data) {
		super(client, data, "/channels");
	}

	@Override
	public String url() {
		return url;
	}
}
