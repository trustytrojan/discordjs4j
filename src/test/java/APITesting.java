import discord.client.UserDiscordClient;
import discord.util.Util;

public class APITesting {
	private static final UserDiscordClient CLIENT = new UserDiscordClient();

	static {
		CLIENT.api.setToken(Util.readFile("tokens/activity-tracker"), true);
	}

	public static void main(String[] args) {
		final var g = CLIENT.guilds.get("1122369288758628464").join();
		System.out.println(g.getData().toPrettyJsonString());
	}
}
