import discord.client.UserDiscordClient;
import discord.util.Util;

final class Test {
	private static final UserDiscordClient CLIENT = new UserDiscordClient();

	static {
		CLIENT.api.setToken(Util.readFile("tokens/main"));
	}

	public static void main(String[] args) throws Exception {
		CLIENT.guilds.refresh(guild -> System.out.printf("id=%s,name=%s\n", guild.id(), guild.name())).join();
	}
}
