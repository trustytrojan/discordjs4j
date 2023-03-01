package log_bot;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.simple.parser.ParseException;

import discord.client.BotDiscordClient;
import discord.util.BetterMap;
import discord.util.JSON;
import discord.util.Util;

public class LogBot extends BotDiscordClient {

	public final BetterMap<String, TGuild> tguilds = new BetterMap<>();

	public LogBot() throws URISyntaxException {
		super();
		try {
			final var objs = JSON.parseObjectArrayFromFile("./tguilds.json");
			for (final var obj : objs) {
				final var tg = new TGuild(obj);
				tguilds.put(tg.guild, tg);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			var data = "[";
			for (final var tg : tguilds)
				data += tg.toJSONString() + ',';
			try {
				Util.writeFile("tguilds.json", data += ']');
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
	}
	
}
