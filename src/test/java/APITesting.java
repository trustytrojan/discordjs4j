import discord.client.UserDiscordClient;
import discord.util.Util;

public class APITesting {
	private static final UserDiscordClient CLIENT = new UserDiscordClient();

	static {
		CLIENT.api.setToken(Util.readFile("tokens/activity-tracker"), true);
	}

	public static void main(String[] args) {
		final var r = CLIENT.api.get("/users/@me/relationships").join();
		System.out.println(r.text);
	}
}
