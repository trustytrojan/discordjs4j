package discord.structures;

import discord.client.DiscordClient;
import discord.managers.GuildChannelManager;
import discord.structures.channels.TextChannel;
import discord.util.CDN;
import discord.util.CDN.URLFactory;
import simple_json.JSONObject;

public class Guild implements DiscordResource {

	private final DiscordClient client;
	private final JSONObject data;

	public final GuildChannelManager channels;

	public Guild(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
		channels = new GuildChannelManager(client, this);
	}

	public String name() {
		return data.getString("name");
	}

	public String systemChannelId() {
		return data.getString("system_channel_id");
	}

	public TextChannel systemChannel() {
		return (TextChannel) client.channels.fetch(systemChannelId());
	}

	public final URLFactory icon = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("icon");
		}

		@Override
		public String url(int size, String extension) {
			return CDN.guildIcon(id(), hash(), size, extension);
		}
	};

	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

}
