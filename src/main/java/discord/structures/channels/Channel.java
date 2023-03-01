package discord.structures.channels;

import java.util.concurrent.CompletableFuture;

import discord.enums.ChannelType;
import discord.structures.DiscordObject;

public interface Channel extends DiscordObject {

	public String url();

	default String mention() {
		return "<#"+id()+'>';
	}

	default String name() {
		return getData().getString("name");
	}

	default ChannelType type() {
		return ChannelType.resolve(getData().getLong("type"));
	}

	default CompletableFuture<Void> delete() {
		return CompletableFuture.runAsync(() -> {
			try {
				final var client = client();
				client.api.delete(api_path());
				client.channels.cache.remove(id());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	default String api_path() {
		return "/channels/"+id();
	}

}
