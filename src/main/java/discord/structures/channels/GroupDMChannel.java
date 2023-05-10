package discord.structures.channels;

import java.util.List;

import simple_json.SjObject;
import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.structures.User;

public class GroupDMChannel implements DMBasedChannel {
	private final DiscordClient client;
	private SjObject data;

	private final MessageManager messages;
	public final List<User> recipients;

	public GroupDMChannel(final DiscordClient client, final SjObject data) {
		this.client = client;
		setData(data);
		messages = new MessageManager(client, this);

		recipients = data.getObjectArray("recipients").parallelStream()
				.map((final var o) -> client.users.fetch(o.getString("id")).join())
				.toList();
	}

	public String icon() {
		return data.getString("icon");
	}

	public String ownerId() {
		return data.getString("owner_id");
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

	@Override
	public MessageManager messages() {
		return messages;
	}
}
