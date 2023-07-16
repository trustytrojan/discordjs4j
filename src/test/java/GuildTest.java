import discord.client.UserDiscordClient;
import discord.util.Util;

public class GuildTest {
	public static void main(String[] args) {
		final var client = new UserDiscordClient();
		client.api.setToken(Util.readFile("tokens/main"));
		client.guilds.refreshCache().join();
		System.out.println("ID,Name");
		for (final var guild : client.guilds) {
			System.out.printf("%s,%s\n", guild.id(), guild.name());
		}
	}
}
