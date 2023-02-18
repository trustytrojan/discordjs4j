package discord.structures;

import java.util.concurrent.CompletableFuture;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.util.JSON;

public interface DiscordObject {

	/**
	 * Allows default method implementations of interfaces
	 * to access the data of this Discord object.
	 */
	public BetterJSONObject getData();

	/**
	 * Set this object's internal data.
	 * @param data new data from Discord
	 */
	public void setData(BetterJSONObject data);

	public DiscordClient client();
	public String api_path();

	default String id() {
		return getData().getString("id");
	}

	default CompletableFuture<Void> fetch() {
		return CompletableFuture.runAsync(() -> {
			try {
				final var raw = client().api.get(api_path());
				final var obj = JSON.parseObject(raw);
				setData(obj);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}

}
