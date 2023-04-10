package discord.managers;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.structures.channels.CategoryChannel;
import discord.structures.channels.Channel;
import discord.structures.channels.DMBasedChannel;
import discord.structures.channels.DMChannel;
import discord.structures.channels.GroupDMChannel;
import discord.structures.channels.TextChannel;
import discord.util.IdMap;
import simple_json.JSONObject;

public class ChannelManager extends DataManager<Channel> {

	public Channel createCorrectChannel(JSONObject data) {
		return switch (Channel.Type.resolve(data.getLong("type"))) {
			case GUILD_TEXT -> new TextChannel(client, data);
			case DM -> new DMChannel(client, data);
			case GROUP_DM -> new GroupDMChannel(client, data);
			case GUILD_CATEGORY -> new CategoryChannel(client, data);
			// ...
			default -> null;
		};
	}

	public ChannelManager(DiscordClient client) {
		super(client);
	}

	@Override
	public Channel construct(JSONObject data) {
		return createCorrectChannel(data);
	}

	// edit group dm channels

	public CompletableFuture<Void> delete(String id) {
		return CompletableFuture.runAsync(() -> client.api.delete("/channels/" + id));
	}

	@Override
	public Channel fetch(String id, boolean force) {
		return super.fetch(id, "/channels/" + id, force);
	}

	public IdMap<DMBasedChannel> fetchDMs() {
		final var channels = new IdMap<DMBasedChannel>();

		for (final var rawChannel : client.api.get("/users/@me/channels").toJSONObjectArray()) {
			final var channel = (DMBasedChannel) cache((JSONObject) rawChannel);
			channels.put(channel);
		}

		return channels;
	}

}
