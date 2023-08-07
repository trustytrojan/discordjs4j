package discord.structures.interactions;

import java.util.Objects;

import discord.client.BotDiscordClient;
import discord.resources.ApplicationCommand;
import discord.structures.ApplicationCommandOption;
import sj.SjObject;

public class ChatInputInteraction extends Interaction {
	public static class Option {
		public final ApplicationCommandOption.Type type;
		public final String name;
		public final Object value;
		public final OptionResolver options;

		public Option(ChatInputInteraction interaction, SjObject data) {
			Objects.requireNonNull(interaction);
			Objects.requireNonNull(data);
			type = ApplicationCommandOption.Type.resolve(data.getLong("type"));
			name = data.getString("name");
			value = data.get("value");
			options = new OptionResolver(interaction, data.getObjectArray("options"));
		}
	}

	public final OptionResolver options;
	public final String commandId;
	public final String commandName;
	public final ApplicationCommand.Type commandType;

	public ChatInputInteraction(BotDiscordClient client, SjObject data) {
		super(client, data);
		commandId = innerData.getString("id");
		commandName = innerData.getString("name");
		commandType = ApplicationCommand.Type.resolve(data.getInteger("type"));
		options = new OptionResolver(this, data.getObjectArray("options"));
	}
}
