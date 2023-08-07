package discord.resources;

import java.util.Collections;
import java.util.Objects;

import discord.client.DiscordClient;
import sj.SjObject;

public abstract class AbstractDiscordResource implements DiscordResource {
	protected final DiscordClient client;
	protected SjObject data;

	private boolean deleted;

	protected AbstractDiscordResource(final DiscordClient client, final SjObject data) {
		this.client = Objects.requireNonNull(client);
		this.data = (SjObject) Collections.unmodifiableMap(data);
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
	public void setData(SjObject data) {
		this.data = data;
	}

	@Override
	public boolean wasDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted() {
		deleted = true;
	}
}
