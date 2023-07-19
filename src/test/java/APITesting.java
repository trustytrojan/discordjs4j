import discord.client.UserDiscordClient;
import discord.util.Util;

public class APITesting {
	private static final UserDiscordClient CLIENT = new UserDiscordClient();

	static {
		CLIENT.api.setToken(Util.readFile("tokens/main"), false);
	}

	public static void main(String[] args) {
		
	}
}
