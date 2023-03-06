package log_bot;

import discord.client.BotDiscordClient;
import discord.util.BetterMap;
import discord.util.JSON;
import discord.util.Util;

public class LogBot extends BotDiscordClient {

	public final BetterMap<String, TGuild> tguilds = new BetterMap<>();

	public LogBot() {
		final var objs = JSON.parseObjectArrayFromFile("./tguilds.json");
		for (final var obj : objs) {
			final var tg = new TGuild(obj);
			tguilds.put(tg.guild, tg);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			var data = "[";
			for (final var tg : tguilds)
				data += tg.toJSONString() + ',';
			Util.writeFile("tguilds.json", data += ']');
		}));
	}
	
}
