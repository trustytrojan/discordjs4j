package discord.resources;

import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.client.DiscordClient;
import discord.managers.ApplicationCommandManager;
import discord.util.CDN;
import discord.util.CDN.AllowedExtension;
import discord.util.CDN.AllowedSize;
import discord.util.CDN.URLFactory;
import sj.SjObject;

/**
 * https://discord.com/developers/docs/resources/application
 */
public class Application extends AbstractDiscordResource {
	public final ApplicationCommandManager commands;

	public Application(DiscordClient client, SjObject data) {
		super(client, data, "/applications");
		commands = new ApplicationCommandManager((BotDiscordClient) client, null);
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

	public final URLFactory icon = new URLFactory() {
		@Override
		public String getHash() {
			return data.getString("icon");
		}

		@Override
		public String makeURL(AllowedSize size, AllowedExtension extension) {
			return CDN.makeApplicationIconURL(id, getHash(), size, extension);
		}
	};
}
