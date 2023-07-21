import java.util.HashMap;
import java.util.Map;

import discord.client.BotDiscordClient;
import discord.resources.interactions.ChatInputInteraction;
import discord.util.Util;
import sj.Sj;
import sj.SjObject;
import sj.SjSerializable;

public class ActivityTracker extends BotDiscordClient {
	private static class ATGuild implements SjSerializable {
		final String guildId;
		final HashMap<String, Integer> activityPerMember;

		ATGuild(String guildId) {
			this.guildId = guildId;
			activityPerMember = new HashMap<>();
		}

		@SuppressWarnings("unchecked")
		ATGuild(SjObject obj) {
			guildId = obj.getString("guild_id");
			activityPerMember = (HashMap<String, Integer>) obj.get("activity_per_member");
		}

		@Override
		public String toJsonString() {
			return Sj.writePretty(
				Map.of(
					"guild_id", guildId,
					"activity_per_member", activityPerMember
				)
			);
		}
	}

	private final HashMap<String, ATGuild> atGuilds = new HashMap<>();

	private ActivityTracker() {
		final var rawAtGuilds = Sj.parseObject(Util.readFile("atguilds.json"));
		rawAtGuilds.keySet().forEach(k -> atGuilds.put(k, new ATGuild(rawAtGuilds.getObject(k))));

		ready.connect(() -> {
			System.out.println("Logged in as " + user.tag() + '!');
			guilds.forEach(g -> { final var id = g.id(); if (!atGuilds.containsKey(id)) atGuilds.put(id, new ATGuild(id)); });
		});

		interactionCreate.connect(i -> {
			if (!(i instanceof final ChatInputInteraction interaction)) return;
			switch (interaction.commandName) {
				case "view_activity" -> {
					
				}
			}
		});
	}
}
