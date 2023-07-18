import discord.client.UserDiscordClient;
import discord.util.Util;

public class APITesting {
	public static void main(String[] args) {
		final var client = new UserDiscordClient();
		client.api.setToken(Util.readFile("tokens/main"), false);
		
	}
}
