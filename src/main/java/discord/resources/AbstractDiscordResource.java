package discord.resources;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import discord.client.DiscordClient;
import sj.SjObject;

public abstract class AbstractDiscordResource implements DiscordResource {
	private final String apiPath;

	protected final DiscordClient client;
	protected SjObject data;

	private final String id;
	private boolean deleted;

	public final Instant createdInstant;

	protected AbstractDiscordResource(DiscordClient client, SjObject data, String baseApiPath) {
		this(client, data, baseApiPath, o -> o.getString("id"));
	}

	protected AbstractDiscordResource(DiscordClient client, SjObject data, String baseApiPath, Function<SjObject, String> idGetter) {
		this.client = client;
		setData(data);
		id = idGetter.apply(data);
		createdInstant = Instant.ofEpochMilli((Long.parseLong(id) >> 22) + 1420070400000L);
		apiPath = baseApiPath + '/' + id;
	}

	public CompletableFuture<Void> refreshData() {
		return client.api.get(apiPath).thenAccept(r -> setData(r.asObject()));
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
		this.data = data;
	}
}
