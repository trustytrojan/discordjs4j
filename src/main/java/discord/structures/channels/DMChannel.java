package discord.structures.channels;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.AbstractDiscordResource;
import discord.structures.User;
import simple_json.SjObject;

public class DMChannel extends AbstractDiscordResource implements TextBasedChannel {
	private final MessageManager messages;
	public final User recipient;
	private final String url = "https://discord.com/channels/@me/" + id;

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

	@Override
	public String url() {
		return url;
	}
}
