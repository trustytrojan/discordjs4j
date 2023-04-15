package discord.managers.guild;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.GuildMember;
import simple_json.JSONObject;

public class GuildMemberManager extends GuildResourceManager<GuildMember> {
	public GuildMemberManager(DiscordClient client, Guild guild) {
		super(client, guild);
	}

	@Override
	public GuildMember construct(JSONObject data) {
		return new GuildMember(client, guild, data);
	}

	@Override
	public CompletableFuture<GuildMember> fetch(String id, boolean force) {
		return super.fetch(id, "/guilds/" + guild.id() + "/members/" + id, force);
	}
}
