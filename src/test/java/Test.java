import discord.client.UserDiscordClient;
import discord.util.Util;

public class Test {
	private static final UserDiscordClient CLIENT = new UserDiscordClient();

	static {
		CLIENT.api.setToken(Util.readFile("tokens/main"), false);
	}

	public static void main(String[] args) {
		final var r = CLIENT.api.put("/users/@me/relationships/1055276108829970472", "{}").join();
		System.out.println(r.body);
	}
}
