package discord.structures.channels;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.User;
import simple_json.SjObject;

public class DMChannel implements DMBasedChannel {
	private final DiscordClient client;
	private SjObject data;

	private final MessageManager messages;
	public final User recipient;

	public DMChannel(final DiscordClient client, final SjObject data) {
		this.client = client;
		setData(data);
		messages = new MessageManager(client, this);
		recipient = client.users.fetch(data.getObjectArray("recipients").get(0).getString("id")).join();
	}

	@Override
	public String toString() {
		return mention();
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
	public SjObject getData() {
		return data;
	}

	@Override
	public void setData(final SjObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}
}
