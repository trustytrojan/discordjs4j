package channel_client;
import discord.client.UserDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.Message;
import discord.resources.channels.MessageChannel;
import discord.resources.guilds.Guild;
import discord.util.Util;

public class UserChannelClient extends UserDiscordClient {
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

	final String clientUserGrayUsername() {
		return Terminal.FG_GRAY + "[" + clientUser.getUsername() + "] " + Terminal.FG_DEFAULT;
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
			System.out.print(clientUserGrayUsername());
			if ((line = console.readLine()) == null) {
				gateway.closeBlocking();
				break;
			}
			if (line.length() > 0)
				channel.send(line);
			System.out.print(Terminal.cursorUp(1) + Terminal.CLEAR_LINE + '\r');
		}
	}

	@Override
	protected void onMessageCreate(final Message message) {
		if (channel == null)
			return;
		if (message.getChannelId() != channel.getId())
			return;
		System.out.print(
			"\r\n" + Terminal.cursorUp(1) + "[" + message.getAuthor().join().getTag() + "] " + message.getContent() + "\n" + clientUserGrayUsername());
	}

	public static void main(String[] args) throws Exception {
		new UserChannelClient(Util.File.read("tokens/alt"));
	}
}
