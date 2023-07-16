package discord.resources;

import discord.client.DiscordClient;
import sj.SjObject;

public abstract class AbstractDiscordResource implements DiscordResource {
	protected final DiscordClient client;
	protected SjObject data;
	protected final String id;

	protected AbstractDiscordResource(DiscordClient client, SjObject data) {
		this.client = client;
		setData(data);
		id = data.getString("id");
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public SjObject getData() {
		return data;
	}

	@Override
	public void setData(SjObject data) {
		this.data = data;
	}
}
