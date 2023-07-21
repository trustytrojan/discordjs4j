import discord.client.DiscordClient;
import discord.util.Util;

public class APITesting {
	private static final DiscordClient CLIENT = new DiscordClient() {
		
	};

	static {
		CLIENT.api.setToken(Util.readFile("tokens/main"), false);
	}

	public static void main(String[] args) {
		final var user = CLIENT.users.get("358402930191106049").join();
		user.setNote("N").join();
	}
}
