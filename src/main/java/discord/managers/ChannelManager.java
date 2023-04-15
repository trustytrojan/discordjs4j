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
import discord.util.Util;
import simple_json.JSONObject;

public class ChannelManager extends ResourceManager<Channel> {
	public ChannelManager(DiscordClient client) {
		super(client);
	}

	@Override
	public Channel construct(JSONObject data) {
		return switch (Channel.Type.resolve(data.getLong("type"))) {
			case GUILD_TEXT -> new TextChannel(client, data);
			case DM -> new DMChannel(client, data);
			case GROUP_DM -> new GroupDMChannel(client, data);
			case GUILD_CATEGORY -> new CategoryChannel(client, data);
			// ...
			default -> null;
		};
	}

	// edit group dm channels

	public CompletableFuture<Void> delete(String id) {
		return client.api.delete("/channels/" + id).thenRunAsync(Util.DO_NOTHING);
	}

	@Override
	public CompletableFuture<Channel> fetch(String id, boolean force) {
		return super.fetch(id, "/channels/" + id, force);
	}

	public CompletableFuture<IdMap<DMBasedChannel>> fetchDMs() {
		final var dms = new IdMap<DMBasedChannel>();

		return CompletableFuture.supplyAsync(() -> {
			for (final var rawDM : client.api.get("/users/@me/channels").join().toJSONObjectArray()) {
				final var channel = (DMBasedChannel) cache((JSONObject) rawDM);
				dms.put(channel);
			}

			return dms;
		});
	}
}
