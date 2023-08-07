package discord.structures.interactions;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.resources.Message;
import discord.structures.components.MessageComponent;
import sj.SjObject;

public class MessageComponentInteraction extends Interaction {
	public final String messageId;
	public final String customId;
	public final MessageComponent.Type componentType;
	public final List<String> values;

	public MessageComponentInteraction(final BotDiscordClient client, final SjObject data) {
		super(client, data);
		messageId = data.getObject("message").getString("id");
		customId = innerData.getString("custom_id");
		componentType = MessageComponent.Type.resolve(innerData.getShort("component_type"));
		switch (componentType) {
			case STRING_SELECT:
			case USER_SELECT:
			case ROLE_SELECT:
			case MENTIONABLE_SELECT:
			case CHANNEL_SELECT:
				values = Collections.unmodifiableList(innerData.getStringArray("values"));
				break;
			default:
				values = Collections.emptyList();
		}
	}

	public CompletableFuture<Message> getMessageAsync() {
		return getChannelAsync().thenCompose(c -> c.getMessageManager().get(messageId));
	}

	public Message getMessage() {
		return getMessageAsync().join();
	}

	public boolean isButton() {
		return (componentType == MessageComponent.Type.BUTTON);
	}

	public boolean isStringSelect() {
		return (componentType == MessageComponent.Type.STRING_SELECT);
	}

	public boolean isRoleSelect() {
		return (componentType == MessageComponent.Type.ROLE_SELECT);
	}

	public boolean isUserSelect() {
		return (componentType == MessageComponent.Type.USER_SELECT);
	}

	public boolean isChannelSelect() {
		return (componentType == MessageComponent.Type.CHANNEL_SELECT);
	}

	public boolean isMentionableSelect() {
		return (componentType == MessageComponent.Type.MENTIONABLE_SELECT);
	}
}
