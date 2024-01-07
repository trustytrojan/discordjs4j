package discord.resources;

import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.managers.ApplicationCommandManager;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.Image;
import sj.SjObject;

/**
 * https://discord.com/developers/docs/resources/application
 */
public class Application extends AbstractDiscordResource {
	public final ApplicationCommandManager commands;

	/**
	 * Only bots should be using this class.
	 */
	public Application(final BotDiscordClient client, final SjObject data) {
		super(client, data);
		commands = new ApplicationCommandManager(client, null);
	}

	@Override
	public String getApiPath() {
		return "/applications/" + getId();
	}

	public CompletableFuture<User> getOwner() {
		return client.users.get(data.getObject("owner").getString("id"));
	}

	public String getName() {
		return data.getString("name");
	}

	public String getDescription() {
		return data.getString("description");
	}

	public final Image icon = new Image() {
		@Override
		public String getHash() {
			return data.getString("icon");
		}

		@Override
		public String getURL(final AllowedSize size, final AllowedExtension extension) {
			return CDN.makeApplicationIconOrCoverURL(getId(), getHash(), size, extension);
		}
	};
}
