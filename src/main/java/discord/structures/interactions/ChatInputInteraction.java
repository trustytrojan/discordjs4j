package discord.structures.interactions;

import java.util.Objects;

import discord.client.BotDiscordClient;
import discord.structures.ApplicationCommand;
import discord.structures.ApplicationCommandOption;
import simple_json.SjObject;

public class ChatInputInteraction extends Interaction {

	public static class Option {
		public final ApplicationCommandOption.Type type;
		public final String name;
		public final Object value;
		public final OptionResolver options;

		public Option(final ChatInputInteraction interaction, final SjObject data) {
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

	public ChatInputInteraction(final BotDiscordClient client, final SjObject data) {
		super(client, data);

		commandId = innerData.getString("id");
		commandName = innerData.getString("name");
		commandType = ApplicationCommand.Type.TYPE_TABLE[innerData.getInteger("type")];
		options = new OptionResolver(this, data.getObjectArray("options"));
	}
}
