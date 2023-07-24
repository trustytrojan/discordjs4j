package discord.resources;

import discord.client.DiscordClient;
import sj.SjObject;

public interface DiscordResource {
	DiscordClient getClient();
	SjObject getData();
	void setData(SjObject data);
	String getId();
	boolean wasDeleted();
	void setDeleted();
}
