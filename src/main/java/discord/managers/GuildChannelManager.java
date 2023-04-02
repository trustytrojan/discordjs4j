package discord.managers;

import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.structures.channels.GuildChannel;
import discord.util.DiscordResourceMap;
import simple_json.JSON;
import simple_json.JSONObject;

public class GuildChannelManager extends DataManager<GuildChannel> {
	
	public final Guild guild;

	public GuildChannelManager(DiscordClient client, Guild guild) {
		super(client);
		this.guild = guild;
	}

	@Override
	public GuildChannel cache(JSONObject data) {
		return cache((GuildChannel) client.channels.createCorrectChannel(data));
	}

	@Override
	public GuildChannel fetch(String id, boolean force) {
		return super.fetch(id, "/channels/" + id, force);
	}

	public DiscordResourceMap<GuildChannel> fetch() {
		final var path = "/guilds/" + guild.id() + "/channels";
		final var channels = new DiscordResourceMap<GuildChannel>();

		for (final var channelData : JSON.parseObjectArray(client.api.get(path))) {
			channels.put((GuildChannel) cache(channelData));
		}

		return channels;
	}

}
