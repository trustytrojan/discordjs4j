package guild_channel_manager;

import javax.swing.JFrame;

import discord.managers.guild.GuildChannelManager;

final class GuildChannelManagerGUI extends JFrame {
	private final GuildChannelManager manager;
	private final GuildChannelsTable table = new GuildChannelsTable();

	GuildChannelManagerGUI(GuildChannelManager manager) {
		super("Guild Channel Manager GUI");
		this.manager = manager;
	}

	
}
