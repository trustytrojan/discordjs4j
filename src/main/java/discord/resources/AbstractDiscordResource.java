package discord.resources;

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;

import discord.client.DiscordClient;
import sj.SjObject;

public abstract class AbstractDiscordResource implements DiscordResource {
	protected final DiscordClient client;
	protected SjObject data;

	public final String id;
	private boolean deleted;

	public final Instant createdInstant;

	protected AbstractDiscordResource(DiscordClient client, SjObject data) {
		this(client, data, data.getString("id"));
	}

	protected AbstractDiscordResource(DiscordClient client, SjObject data, String id) {
		this.client = Objects.requireNonNull(client);
		setData(data);
		this.id = Objects.requireNonNull(id);
		createdInstant = Instant.ofEpochMilli((Long.parseLong(id) >> 22) + 1420070400000L);
	}

	@Override
	public boolean wasDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted() {
		deleted = true;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public DiscordClient getClient() {
		return client;
	}

	@Override
	public SjObject getData() {
		return (SjObject) Collections.unmodifiableMap(data);
	}

	@Override
	public void setData(SjObject data) {
		this.data = Objects.requireNonNull(data);
	}
}
