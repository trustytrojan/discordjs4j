package discord.resources.guilds;

import discord.client.DiscordClient;
import discord.enums.Permission;
import discord.util.BitFlagSet;
import sj.SjObject;

public class CurrentUserGuild extends BaseGuild {
	public CurrentUserGuild(DiscordClient client, SjObject data) {
		super(client, data);
	}

	public boolean currentUserIsOwner() {
		return data.getBoolean("owner");
	}

	public BitFlagSet<Permission> getCurrentUserPermissions() {
		return new BitFlagSet<>(Long.parseLong(data.getString("permissions")));
	}
}
