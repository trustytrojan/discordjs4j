package channel_client;
import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.Message;
import discord.resources.channels.MessageChannel;
import discord.resources.guilds.Guild;
import discord.util.Util;

public class BotChannelClient extends BotDiscordClient {
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
			System.out.print(Terminal.cursorUp(1) + Terminal.CLEAR_LINE + '\r');
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
		return Terminal.FG_GRAY + "[" + clientUser.getTag() + "] " + Terminal.FG_DEFAULT;
	}

	@Override
	protected void onMessageCreate(final Message message) {
		if (channel == null)
			return;
		if (message.getChannelId() != channel.getId())
			return;
		System.out.print(
			"\r\n" + Terminal.cursorUp(1) + "[" + message.getAuthor().join().getTag() + "] " + message.getContent() + "\n" + clientUserGrayTag());
	}

	public static void main(final String[] args) throws Exception {
		new BotChannelClient(Util.readFile("tokens/bot"));
	}
}
