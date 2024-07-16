import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.Message;
import discord.resources.channels.MessageChannel;
import discord.resources.guilds.Guild;
import discord.util.Util;

public class BotChannelClient extends BotDiscordClient {
	/**
	 * To make life easier we will use {@code fzf} to allow the user to select a guild and channel to use.
	 */
	private static class FzfProcess {
		final Process process;
		final BufferedWriter stdin;
		final CompletableFuture<String> result;

		FzfProcess(String header) throws IOException {
			process = new ProcessBuilder("fzf", "--header", header).start();
			stdin = process.outputWriter();
			result = CompletableFuture.supplyAsync(() -> {
				try { return process.inputReader().readLine(); }
				catch (final Exception e) { e.printStackTrace(); return null; }
			});
		}

		void addChoice(String choice) throws IOException {
			stdin.write(choice + '\n');
			stdin.flush();
		}
	}

	Guild guild;
	MessageChannel channel;
	FzfProcess fzf;
	boolean gettingGuilds = true;

	public BotChannelClient(final String token) throws Exception {
		super(token);
		gateway.tryConnect();
		gateway.identify(
			GatewayIntent.GUILDS,
			GatewayIntent.GUILD_MESSAGES
		);

		// select guild (letting the guilds come from the gateway since we are a bot user; see onGuildCreate below)
		fzf = new FzfProcess("Select guild");
		guild = guilds.get(fzf.result.join().split(" ")[0]).join();
		gettingGuilds = false;

		// select channel
		fzf = new FzfProcess("Select channel");
		guild.channels.refreshCache().join();
		for (final var channel : guild.channels.cache.values())
			fzf.addChoice(channel.getId() + ' ' + channel.getName());
		channel = (MessageChannel) guild.channels.get(fzf.result.join().split(" ")[0]).join();

		// get console
		final var console = System.console();
		if (console == null)
			System.exit(1);

		// message send loop
		String line;
		while (true) {
			System.out.print(clientUserGrayTag());
			if ((line = console.readLine()) == null) {
				gateway.closeBlocking();
				break;
			}
			channel.send(line);
			System.out.print(cursorUp(1) + clearLine() + '\r');
		}
	}

	/**
	 * As a bot user, full guild objects don't arrive in the Ready event.
	 * They come one-by-one via Guild Create events.
	 * We use a boolean flag {@code gettingGuilds} to control when to stop processing guilds.
	 */
	@Override
	protected void onGuildCreate(final Guild guild) {
		if (gettingGuilds)
			try {
				fzf.addChoice(guild.getId() + ' ' + guild.getName());
			} catch (final Exception e) {
				e.printStackTrace();
			}
	}

	final String clientUserGrayTag() {
		return fgGray() + "[" + clientUser.getTag() + "] " + fgDefault();
	}

	final char ESCAPE = 27;

	final String cursorUp(final int lines) {
		return ESCAPE + "[" + lines + 'A';
	}

	final String clearLine() {
		return ESCAPE + "[2K";
	}

	final String fgGray() {
		return ESCAPE + "[30m";
	}

	final String fgDefault() {
		return ESCAPE + "[39m";
	}

	final String fgReset() {
		return ESCAPE + "[0m";
	}

	@Override
	protected void onMessageCreate(final Message message) {
		if (channel == null)
			return;
		if (message.getChannelId() != channel.getId())
			return;
		System.out.print(
			"\r\n" + cursorUp(1) + "[" + message.getAuthor().join().getTag() + "] " + message.getContent() + "\n" + clientUserGrayTag());
	}

	public static void main(final String[] args) throws Exception {
		new BotChannelClient(Util.readFile("tokens/bot"));
	}
}
