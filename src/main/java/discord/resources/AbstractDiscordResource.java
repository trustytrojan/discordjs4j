package discord.resources;

import java.util.Objects;

import discord.client.DiscordClient;
import sj.SjObject;

public abstract class AbstractDiscordResource implements DiscordResource {
	protected final DiscordClient client;
	protected SjObject data;

	private boolean deleted;

	protected AbstractDiscordResource(final DiscordClient client, final SjObject data) {
		this.client = Objects.requireNonNull(client);
		setData(data);
	}

	@Override
	public DiscordClient getClient() {
		return client;
	}

	@Override
	public SjObject getData() {
		return data;
	}

	@Override
	public void setData(final SjObject data) {
		this.data = Objects.requireNonNull(data);
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public void markAsDeleted() {
		deleted = true;
	}
}
