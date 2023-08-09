package discord.structures.interactions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import discord.resources.DiscordResource;
import discord.resources.GuildMember;
import discord.resources.Role;
import discord.resources.User;
import discord.resources.channels.GuildChannel;
import sj.SjObject;

public class OptionResolver implements Iterable<ChatInputInteraction.Option> {
	private final ChatInputInteraction interaction;
	private final Map<String, ChatInputInteraction.Option> options;

	OptionResolver(final ChatInputInteraction interaction, final List<SjObject> rawOptions) {
		this.interaction = Objects.requireNonNull(interaction);

		options = new HashMap<String, ChatInputInteraction.Option>();

		if (rawOptions != null) {
			for (final var optionData : rawOptions) {
				final var option = new ChatInputInteraction.Option(interaction, optionData);
				options.put(option.name, option);
			}
		}
	}

	@Override
	public Iterator<ChatInputInteraction.Option> iterator() {
		return (options == null)
				? Collections.emptyIterator()
				: options.values().iterator();
	}

	public ChatInputInteraction.Option get(final String optionName) {
		return options.get(optionName);
	}

	private <T extends DiscordResource> CompletableFuture<T> getResource(final String optionName,
			final Function<String, CompletableFuture<T>> resourceGetter) {
		final var id = getString(optionName);
		return (id == null)
				? CompletableFuture.completedFuture(null)
				: resourceGetter.apply(id);
	}

	public CompletableFuture<Role> getRoleAsync(final String optionName) {
		return interaction.getGuildAsync().thenCompose(g -> getResource(optionName, g.roles::get));
	}

	public CompletableFuture<User> getUserAsync(final String optionName) {
		return getResource(optionName, interaction.client.users::get);
	}

	public CompletableFuture<GuildMember> getMemberAsync(final String optionName) {
		return interaction.getGuildAsync().thenCompose(g -> getResource(optionName, g.members::get));
	}

	public CompletableFuture<GuildChannel> getChannelAsync(final String optionName) {
		return interaction.getGuildAsync().thenCompose(g -> getResource(optionName, g.channels::get));
	}

	/**
	 * If a subcommand was used, it will be the only option
	 * 
	 * @return the name of the subcommand
	 */
	public ChatInputInteraction.Option getSubcommand() {
		return options.values().iterator().next();
	}

	public String getString(final String optionName) {
		final var option = get(optionName);
		return (option == null) ? null : ((String) option.value);
	}

	public Long getInteger(final String optionName) {
		final var option = get(optionName);
		return (option == null) ? null : ((Long) option.value);
	}

	public Double getDouble(final String optionName) {
		final var option = get(optionName);
		return (option == null) ? null : ((Double) option.value);
	}

	public Boolean getBoolean(final String optionName) {
		final var option = get(optionName);
		return (option == null) ? null : ((Boolean) option.value);
	}
}
