package discord.structures.channels;

import java.util.ArrayList;
import java.util.List;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.User;

public class GroupDMChannel implements TextBasedChannel {

	private final DiscordClient client;
	private BetterJSONObject data;
	private final MessageManager messages;
	private final List<User> recipients = new ArrayList<>();

	public GroupDMChannel(DiscordClient client, BetterJSONObject data) {
		this.client = client;
		setData(data);
		messages = new MessageManager(client, this);
		updateRecipients(data.getObjectArray("recipients"));
	}

	@Override
	public String url() {
		return "https://discord.com/channels/@me/"+id();
	}

	@Override
	public String toString() {
		return mention();
	}

	public String name() {
		return data.getString("name");
	}

	public String icon() {
		return data.getString("icon");
	}

	public String last_message_id() {
		return data.getString("last_message_id");
	}

	public String owner_id() {
		return data.getString("owner_id");
	}

	public List<User> recipients() {
		return recipients;
	}

	public void updateRecipients(List<BetterJSONObject> raw_recipients) {
		for (final var raw_user : raw_recipients)
			recipients.add(client.users.cache(raw_user));
	}

	@Override
	public BetterJSONObject getData() {
		return data;
	}

	@Override
	public void setData(BetterJSONObject data) {
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
