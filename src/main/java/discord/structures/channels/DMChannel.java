package discord.structures.channels;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.User;
import simple_json.JSONObject;

public class DMChannel implements DMBasedChannel {
	private final DiscordClient client;
	private JSONObject data;

	private final MessageManager messages;
	public final User recipient;

	public DMChannel(final DiscordClient client, final JSONObject data) {
		this.client = client;
		this.data = data;
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
	public JSONObject getData() {
		return data;
	}

	@Override
	public void setData(final JSONObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}
}
