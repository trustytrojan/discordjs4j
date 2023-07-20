import discord.client.UserDiscordClient;
import discord.util.Util;

public class APITesting {
	private static final UserDiscordClient CLIENT = new UserDiscordClient();

	static {
		CLIENT.api.setToken(Util.readFile("tokens/alt"), false);
	}

	public static void main(String[] args) {
		final var user = CLIENT.users.get("239743430899531777").join();
		user.addFriend().join();
	}
}
