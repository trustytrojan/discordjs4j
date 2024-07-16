package discord.resources.channels;

import discord.client.DiscordClient;
import discord.resources.guilds.Guild;
import sj.SjObject;

public class AnnouncementChannel extends TextChannel {
	AnnouncementChannel(DiscordClient client, SjObject data) {
		super(client, data);
		//TODO Auto-generated constructor stub
	}

	AnnouncementChannel(DiscordClient client, SjObject data, Guild guild) {
		super(client, data, guild);
		//TODO Auto-generated constructor stub
	}
}
