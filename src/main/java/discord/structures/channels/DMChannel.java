package discord.structures.channels;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.User;
import sj.SjObject;

public class DMChannel extends DMBasedChannel implements TextBasedChannel {
	private final MessageManager messages;
	public final User recipient;

	public DMChannel(DiscordClient client, SjObject data) {
		super(client, data);
		messages = new MessageManager(client, this);
		recipient = client.users.fetch(data.getObjectArray("recipients").get(0).getString("id")).join();
	}

	@Override
	public MessageManager messages() {
		return messages;
	}

	@Override
	public String name() {
		return "DM with " + recipient.tag();
	}

	public String lastMessageId() {
		return data.getString("last_message_id");
	}
}
