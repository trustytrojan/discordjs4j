import discord.client.UserDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.Message;
import discord.util.Util;

public class SelfBot extends UserDiscordClient {
	// private static final String PRIVATE_GUILD_ID = "1131342149301055488";
	private static final String PREFIX = "!";

	private SelfBot(String token) {
		super(token, false);
		Runtime.getRuntime().addShutdownHook(new Thread(gateway::close));
		gateway.connectAndIdentify(
			GatewayIntent.GUILDS,
			GatewayIntent.GUILD_MESSAGES);
	}

	@Override
	protected void onReady() {
		System.out.println("Logged in as " + user.getTag() + '!');
	}

	@Override
	protected void onMessageCreate(Message message) {
		if (message.author != user)
			return;
		final var content = message.getContent();
		final var args = content.split(" ");
		if (!args[0].startsWith(PREFIX))
			return;
		switch (args[0].substring(1)) {
			case "list" -> {
				switch (args[1]) {
					case "channels" -> {
						if (!message.inGuild)
							return;
						final var categoryChannels = message.guild.channels.getCategoryChannels();
						message.channel.send(categoryChannels.toString());
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new SelfBot(Util.readFile("tokens/main"));
	}
}
