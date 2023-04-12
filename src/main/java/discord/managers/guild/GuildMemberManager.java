package discord.managers.guild;

import discord.client.DiscordClient;
import discord.managers.DataManager;
import discord.structures.Guild;
import discord.structures.GuildMember;
import simple_json.JSONObject;

public class GuildMemberManager extends GuildDataManager<GuildMember> {
	public GuildMemberManager(DiscordClient client, Guild guild) {
		super(client, guild);
	}

	@Override
	public GuildMember construct(JSONObject data) {
		return new GuildMember(client, guild, data);
	}

	@Override
	public GuildMember fetch(String id, boolean force) {
		return super.fetch(id, "/guilds/" + guild.id() + "/members/" + id, force);
	}

	
}
