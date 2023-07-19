package discord.resources;

import discord.client.DiscordClient;
import sj.SjObject;

public interface DiscordResource {
	DiscordClient client();

	SjObject getData();

	void setData(SjObject data);

	String id();
}
