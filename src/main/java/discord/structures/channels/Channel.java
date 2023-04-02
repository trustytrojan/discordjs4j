package discord.structures.channels;

import java.util.concurrent.CompletableFuture;

import discord.enums.ChannelType;
import discord.structures.DiscordResource;
import discord.structures.payloads.ChannelPayload;

public interface Channel extends DiscordResource {

	public String url();

	default String mention() {
		return "<#" + id() + '>';
	}

	default String name() {
		return getData().getString("name");
	}

	default ChannelType type() {
		return ChannelType.resolve(getData().getLong("type"));
	}

	default CompletableFuture<Channel> edit(ChannelPayload payload) {
		return client().channels.edit(id(), payload);
	}

	default CompletableFuture<Void> delete() {
		return client().channels.delete(id());
	}

}
