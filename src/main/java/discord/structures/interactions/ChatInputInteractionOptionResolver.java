package discord.structures.interactions;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import discord.structures.GuildMember;
import discord.structures.Role;
import discord.structures.User;
import discord.structures.channels.GuildChannel;
import simple_json.JSONObject;

public class ChatInputInteractionOptionResolver {
	private final ChatInputInteraction interaction;
	private final HashMap<String, ChatInputInteractionOption> options = new HashMap<>();

	ChatInputInteractionOptionResolver(final ChatInputInteraction interaction, final List<JSONObject> rawOptions) {
		Objects.requireNonNull(interaction);
		Objects.requireNonNull(rawOptions);

		this.interaction = interaction;
		for (final var optionData : rawOptions) {
			final var option = new ChatInputInteractionOption(interaction, optionData);
			options.put(option.name, option);
		}
	}

	public ChatInputInteractionOption get(final String optionName) {
		return options.get(optionName);
	}

	public CompletableFuture<Role> getRole(final String optionName) {
		final var id = getString(optionName);
		if (id == null)
			return null;
		return interaction.guild.roles.fetch(id);
	}

	public CompletableFuture<User> getUser(final String optionName) {
		final var id = getString(optionName);
		if (id == null)
			return null;
		return interaction.client.users.fetch(id);
	}

	public CompletableFuture<GuildMember> getMember(final String optionName) {
		final var id = getString(optionName);
		if (id == null)
			return null;
		return interaction.guild.members.fetch(id);
	}

	public CompletableFuture<GuildChannel> getChannel(final String optionName) {
		final var id = getString(optionName);
		if (id == null)
			return null;
		return interaction.guild.channels.fetch(id);
	}

	/**
	 * If a subcommand was used, it will be the only option
	 * 
	 * @return the name of the subcommand
	 */
	public ChatInputInteractionOption getSubcommand() {
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
