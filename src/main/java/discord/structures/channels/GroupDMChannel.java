package discord.structures.channels;

import java.util.LinkedList;

import simple_json.JSONObject;
import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.User;

public class GroupDMChannel implements DMBasedChannel {

	private final DiscordClient client;
	private JSONObject data;

	private final MessageManager messages;

	public final User[] recipients;

	public GroupDMChannel(DiscordClient client, JSONObject data) {
		this.client = client;
		this.data = data;
		messages = new MessageManager(client, this);

		final var recipients = new LinkedList<User>();
		for (final var recipientData : data.getObjectArray("recipients")) {
			recipients.add(client.users.fetch(recipientData.getString("id")).join());
		}
		this.recipients = (User[]) recipients.toArray();
	}

	public String icon() {
		return data.getString("icon");
	}

	public String ownerId() {
		return data.getString("owner_id");
	}

	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public void setData(JSONObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

	@Override
	public MessageManager messages() {
		return messages;
	}

}
