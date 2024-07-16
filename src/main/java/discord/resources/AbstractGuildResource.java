package discord.resources;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.resources.guilds.Guild;
import sj.SjObject;

/**
 * Abstract class for {@link GuildResource}.
 * <p>
 * @apiNote
 * Subclasses should implement both constructors. There are cases when
 * the guild-resource object itself has no {@code guild_id} property;
 * this usually occurs when you receive a guild object with referential
 * data like channels, roles, etc. Usually in this scenario you would have
 * already constructed a {@link Guild} object, so you should use the
 * {@link #AbstractGuildResource(DiscordClient, SjObject, Guild)} constructor.
 * <p>
 * Otherwise, when the guild-resource object does have a {@code guild_id} property,
 * the {@link #AbstractGuildResource(DiscordClient, SjObject)} constructor can be used instead.
 */
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
	public CompletableFuture<Guild> getGuild() {
		return CompletableFuture.completedFuture(guild);
	}
}
