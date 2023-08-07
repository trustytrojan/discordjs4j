package discord.resources.channels;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.resources.AbstractDiscordResource;
import discord.resources.Message;
import discord.resources.User;
import sj.SjObject;

public class DMChannel extends AbstractDiscordResource implements NonGuildChannel, MessageChannel {
	private final MessageManager messages;

	public DMChannel(DiscordClient client, SjObject data) {
		super(client, data);
		messages = new MessageManager(client, this);
	}

	@Override
	public String getName() {
		return "DM with " + getRecipientAsync().join().getTag();
	}

	public CompletableFuture<User> getRecipientAsync() {
		return client.users.get(data.getObjectArray("recipients").get(0).getString("id"));
	}

	public String getLastMessageId() {
		return data.getString("last_message_id");
	}

	public CompletableFuture<Message> getLastMessage() {
		return messages.get(getLastMessageId());
	}

	@Override
	public MessageManager getMessageManager() {
		return messages;
	}
}
