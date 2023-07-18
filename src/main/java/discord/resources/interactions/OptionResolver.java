package discord.resources.interactions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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

		if (rawOptions == null)
			options = null;
		else {
			final var options = new HashMap<String, ChatInputInteraction.Option>();

			for (final var optionData : rawOptions) {
				final var option = new ChatInputInteraction.Option(interaction, optionData);
				options.put(option.name, option);
			}

			this.options = Collections.unmodifiableMap(options);
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

	public CompletableFuture<Role> getRole(final String optionName) {
		final var id = getString(optionName);
		if (id == null)
			return null;
		return interaction.guild.roles.get(id);
	}

	public CompletableFuture<User> getUser(final String optionName) {
		final var id = getString(optionName);
		if (id == null)
			return null;
		return interaction.client.users.get(id);
	}

	public CompletableFuture<GuildMember> getMember(final String optionName) {
		final var id = getString(optionName);
		if (id == null)
			return null;
		return interaction.guild.members.get(id);
	}

	public CompletableFuture<GuildChannel> getChannel(final String optionName) {
		final var id = getString(optionName);
		if (id == null)
			return null;
		return interaction.guild.channels.get(id);
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
