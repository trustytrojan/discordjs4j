import discord.client.UserDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.Message;
import discord.util.Util;

public final class SelfBot extends UserDiscordClient {
	// private static final String PRIVATE_GUILD_ID = "1131342149301055488";
	// 1131342149301055488
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
		System.out.println("Logged in as " + currentUser.getTag() + '!');
	}

	@Override
	protected void onMessageCreate(Message message) {
		if (!message.getAuthorId().equals(currentUser.getId()))
			return;
		final var guild = message.getGuildAsync().join();
		final var channel = message.getChannelAsync().join();
		final var content = message.getContent();
		final var args = content.split(" ");
		if (!args[0].startsWith(PREFIX))
			return;
		switch (args[0].substring(1)) {
			case "list" -> {
				switch (args[1]) {
					case "channels" -> {
						if (guild == null)
							return;
						guild.channels.refreshCache().join();
						final var categoryChannels = guild.channels.getCategoryChannels();
						System.out.println(categoryChannels);
						System.out.println(guild.channels.cache);
						channel.send(categoryChannels.toString());
					}

					case "roles" -> {
						if (guild == null)
							return;
						guild.roles.refreshCache().join();
						final var sb = new StringBuilder("```Id                 \tName\n");
						guild.roles.cache.values().forEach(r -> sb.append(r.getId()).append('\t').append(r.getName()).append('\n'));
						message.reply(sb.append("```").toString());
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new SelfBot(Util.readFile("tokens/main"));
	}
}