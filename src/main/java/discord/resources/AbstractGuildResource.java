package discord.resources;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.guilds.Guild;
import sj.SjObject;

public abstract class AbstractGuildResource extends AbstractDiscordResource implements GuildResource {
	protected final Guild guild;

	protected AbstractGuildResource(DiscordClient client, SjObject data) {
		super(client, data);
		this.guild = client.guilds.get(data.getString("guild_id")).join();
	}

	protected AbstractGuildResource(DiscordClient client, SjObject data, Guild guild) {
		super(client, data);
		this.guild = Objects.requireNonNull(guild);
	}

	@Override
	public String getGuildId() {
		return guild.getId();
	}

	@Override
	public CompletableFuture<Guild> getGuildAsync() {
		return CompletableFuture.completedFuture(guild);
	}
}
