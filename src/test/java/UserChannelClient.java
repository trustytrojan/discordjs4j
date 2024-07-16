import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import discord.client.UserDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.Message;
import discord.resources.channels.MessageChannel;
import discord.resources.guilds.Guild;
import discord.util.Util;

public class UserChannelClient extends UserDiscordClient {
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
				catch (final IOException e) { e.printStackTrace(); System.exit(1); return null; }
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

	public UserChannelClient(String token) throws Exception {
		super(token);
		gateway.tryConnect();
		gateway.identify(
			GatewayIntent.GUILDS,
			GatewayIntent.GUILD_MESSAGES
		);
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

	/**
	 * As a user account, the Ready event sends all the guilds and channels
	 * that the user is in, so this is all we need to wait for. We offload
	 * the input loop to a thread since the thread calling {@link #onReady}
	 * is the WebSocket thread; we don't want to block it.
	 */
	@Override
	public void onReady() {
		Thread.ofVirtual().start(() -> {
			try { start(); }
			catch (final Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		});
	}

	private void start() throws Exception {
		// select guild
		fzf = new FzfProcess("Select guild");
		for (final var guild : guilds.cache.values())
			fzf.addChoice(guild.getId() + ' ' + guild.getName());
		guild = guilds.get(fzf.result.join().split(" ")[0]).join();

		// select channel
		fzf = new FzfProcess("Select channel");
		for (final var channel : guild.channels.getMessageChannels())
			fzf.addChoice(channel.getId() + " \"" + channel.getName() + "\" (" + channel.getType() + ")");
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
			if (line.length() > 0)
				channel.send(line);
			System.out.print(cursorUp(1) + clearLine() + '\r');
		}
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

	public static void main(String[] args) throws Exception {
		new UserChannelClient(Util.readFile("tokens/alt"));
	}
}
