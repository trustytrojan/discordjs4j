import discord.client.UserDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.Message;
import discord.util.Util;

public class SelfBot extends UserDiscordClient {
	//private static final String PRIVATE_GUILD_ID = "1131342149301055488";

	private SelfBot(String token) {
		super(token, true);
		Runtime.getRuntime().addShutdownHook(new Thread(gateway::close));
		gateway.connectAndIdentify(
			GatewayIntent.DIRECT_MESSAGES);
	}

	@Override
	protected void onReady() {
		System.out.println("Logged in as " + user.getTag() + '!');
	}

	@Override
	protected void onMessageCreate(Message message) {
		if (message.author != user) return;
		System.out.println("Received message from myself: " + message.getContent());
	}

	public static void main(String[] args) {
		new SelfBot(Util.readFile("tokens/main"));
	}
}
