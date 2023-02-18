package discord.structures.channels;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.structures.Guild;

public class CategoryChannel implements GuildChannel {

	private final DiscordClient client;
	private BetterJSONObject data;

	public CategoryChannel(DiscordClient client, BetterJSONObject data) {
		this.client = client;
		this.data = data;
	}

	@Override
	public String name() {
		return data.getString("name");
	}

	@Override
	public String guild_id() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Guild guild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BetterJSONObject getData() {
		return data;
	}

	@Override
	public void setData(BetterJSONObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public Long position() {
		return data.getLong("position");
	}

}
