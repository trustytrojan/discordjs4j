package discord.structures;

import discord.client.BotDiscordClient;
import discord.client.DiscordClient;
import discord.managers.ApplicationCommandManager;
import discord.managers.guild.GuildChannelManager;
import discord.managers.guild.GuildMemberManager;
import discord.managers.guild.RoleManager;
import discord.structures.channels.TextChannel;
import discord.util.CDN;
import discord.util.CDN.URLFactory;
import simple_json.SjObject;

public class Guild extends AbstractDiscordResource {
	public final GuildMemberManager members;
	public final GuildChannelManager channels;
	public final RoleManager roles;
	public final ApplicationCommandManager commands;

	public Guild(DiscordClient client, SjObject data) {
		super(client, data);
		channels = new GuildChannelManager(client, this);
		roles = new RoleManager(client, this);
		members = new GuildMemberManager(client, this);
		commands = (client instanceof BotDiscordClient)
				? new ApplicationCommandManager((BotDiscordClient) client, id)
				: null;
	}

	public String name() {
		return data.getString("name");
	}

	public String systemChannelId() {
		return data.getString("system_channel_id");
	}

	public TextChannel systemChannel() {
		return (TextChannel) client.channels.fetch(systemChannelId()).join();
	}

	public final URLFactory icon = new URLFactory() {
		@Override
		public String hash() {
			return data.getString("icon");
		}

		@Override
		public String url(int size, String extension) {
			return CDN.guildIcon(id, hash(), size, extension);
		}
	};

	@Override
	public String apiPath() {
		return "/guilds/" + id;
	}
}
