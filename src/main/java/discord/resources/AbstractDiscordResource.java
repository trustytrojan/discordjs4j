package discord.resources;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import sj.SjObject;

public abstract class AbstractDiscordResource implements DiscordResource {
	protected final DiscordClient client;
	protected SjObject data;
	protected final String id;
	protected boolean deleted;
	private final String apiPath;

	protected AbstractDiscordResource(DiscordClient client, SjObject data, String baseApiPath) {
		this.client = client;
		setData(data);
		id = data.getString("id");
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
		return data;
	}

	@Override
	public void setData(SjObject data) {
		this.data = data;
	}
}
