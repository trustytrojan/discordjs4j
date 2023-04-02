package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.Guild;
import discord.util.DiscordResourceMap;
import simple_json.JSON;
import simple_json.JSONObject;

public class GuildManager extends DataManager<Guild> {

	public GuildManager(DiscordClient client) {
		super(client);
	}

	@Override
	public Guild cache(JSONObject data) {
		return cache(new Guild(client, data));
	}

	@Override
	public Guild fetch(String id, boolean force) {
		return super.fetch(id, "/guilds/" + id, force);
	}

	public DiscordResourceMap<Guild> fetch() {
		final var guilds = new DiscordResourceMap<Guild>();

		for (final var partialGuild : JSON.parseObjectArray(client.api.get("/users/@me/guilds"))) {
			guilds.put(client.guilds.fetch(partialGuild.getString("id")));
		}

		return guilds;
	}

	public CompletableFuture<DiscordResourceMap<Guild>> fetchAsync() {
		return CompletableFuture.supplyAsync(this::fetch);
	}

}
