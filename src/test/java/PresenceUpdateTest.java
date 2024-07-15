import java.util.List;

import discord.client.BotDiscordClient;
import discord.client.UserDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.User;
import discord.structures.Activity;
import discord.structures.ClientStatus;
import discord.util.Util;
import sj.Sj;

public final class PresenceUpdateTest {
	private static class BotTest extends BotDiscordClient {
		public BotTest(final String token) throws Exception {
			super(token, true);
			gateway.tryConnect();
			gateway.identify(GatewayIntent.GUILD_PRESENCES);
		}

		@Override
		protected void onPresenceUpdate(
			final User user,
			final String guildId,
			final String status,
			final List<Activity> activities,
			final ClientStatus clientStatus
		) {
			System.out.println(user.getTag() + " is now " + status);
			System.out.println("this event was received in guild " + guildId);
			if (activities != null)
				for (final var activity : activities)
					System.out.println(activity);
		}
	}

	private static class UserTest extends UserDiscordClient {
		public UserTest(final String token) throws Exception {
			super(token, true);
			gateway.tryConnect();
			gateway.identify(GatewayIntent.GUILD_PRESENCES);
		}

		@Override
		protected void onPresenceUpdate(
			final User user,
			final String guildId,
			final String status,
			final List<Activity> activities,
			final ClientStatus clientStatus
		) {
			System.out.println("\t" + user.getTag() + " is now " + status);
			System.out.println("\tthis event was received in guild " + guildId);
			if (activities != null)
				System.out.println(Sj.writePretty(activities));
		}
	}

	public static void main(String[] args) throws Exception {
		// new BotTest(Util.readFile("tokens/bot"));
		new UserTest(Util.readFile("tokens/alt"));
	}
}
