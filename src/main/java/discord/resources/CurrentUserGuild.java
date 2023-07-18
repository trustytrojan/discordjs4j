package discord.resources;

import discord.client.DiscordClient;
import sj.SjObject;

public class CurrentUserGuild extends BaseGuild {
	public CurrentUserGuild(DiscordClient client, SjObject data) {
		super(client, data);
	}

	public boolean owner() {
		return data.getBoolean("owner");
	}

	public Permissions permissions() {
		return new Permissions(Long.parseLong(data.getString("permissions")));
	}
}
