package discord.resources.channels;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.resources.AbstractDiscordResource;
import discord.resources.User;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.Image;
import sj.SjObject;
import sj.SjSerializable;

public class GroupDMChannel extends AbstractDiscordResource implements NonGuildChannel, MessageChannel {
	public static class Payload implements SjSerializable {
		public String name;
		private String iconBase64;

		public void encodeIconFile(Path path) {
			try {
				final var bytes = Files.readAllBytes(path);
				iconBase64 = Base64.getEncoder().encodeToString(bytes);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void encodeIconFile(File file) {
			encodeIconFile(file.toPath());
		}

		public void encodeIconFile(String path) {
			encodeIconFile(Path.of(path));
		}

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			if (name != null)
				obj.put("name", name);
			if (iconBase64 != null)
				obj.put("icon", iconBase64);
			return obj.toJsonString();
		}
	}

	private final MessageManager messages;
	public final List<User> recipients;
	public final User owner;

	public GroupDMChannel(DiscordClient client, SjObject data) {
		super(client, data);
		messages = new MessageManager(client, this);
		owner = client.users.get(data.getString("owner_id")).join();
		recipients = data.getObjectArray("recipients").parallelStream()
				.map(o -> client.users.get(o.getString("id")).join())
				.toList();
	}

	public CompletableFuture<GroupDMChannel> edit(Payload payload) {
		return client.channels.editGroupDM(getId(), payload);
	}

	private CompletableFuture<Void> leave(boolean silent) {
		return client.api.delete("/channels/" + getId() + "?silent=" + silent)
			.thenRun(() -> client.channels.cache.remove(getId()));
	}

	public CompletableFuture<Void> leave() {
		return leave(false);
	}

	public CompletableFuture<Void> leaveSilently() {
		return leave(true);
	}

	public final Image icon = new Image() {
		@Override
		public String getHash() {
			return data.getString("icon");
		}

		@Override
		public String getURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeChannelIconURL(getId(), getHash(), size, extension);
		}
	};

	@Override
	public MessageManager getMessageManager() {
		return messages;
	}
}
