package discord.structures.channels;

import discord.client.DiscordClient;
import discord.structures.AbstractDiscordResource;
import simple_json.SjObject;

public class DMBasedChannel extends AbstractDiscordResource implements Channel {
	private final String url = "https://discord.com/channels/@me/" + id;

	protected DMBasedChannel(DiscordClient client, SjObject data) {
		super(client, data);
	}

	@Override
	public String url() {
		return url;
	}
}
