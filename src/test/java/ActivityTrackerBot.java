import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.ApplicationCommand;
import discord.resources.ApplicationCommandOption;
import discord.resources.Embed;
import discord.resources.Message;
import discord.resources.channels.MessageChannel;
import discord.resources.guilds.Guild;
import discord.resources.interactions.ChatInputInteraction;
import discord.resources.interactions.Interaction;
import discord.util.Util;
import sj.Sj;
import sj.SjObject;
import sj.SjSerializable;

public class ActivityTrackerBot extends BotDiscordClient {
	private class ActivityData implements SjSerializable {
		Message lastMessage;
		long messageCount;
		long minutesActive;

		ActivityData(Message message) {
			lastMessage = message;
		}

		ActivityData(SjObject data) {
			final var lastMessageArray = data.getStringArray("last_message");
			final var channel = (MessageChannel) channels.get(lastMessageArray.get(0)).join();
			lastMessage = channel.messages().get(lastMessageArray.get(1)).join();
			messageCount = data.getLong("message_count");
			minutesActive = data.getLong("minutes_active");
		}

		void incrementMessageCount() {
			messageCount += 1;
		}

		void incrementMinutesActive() {
			minutesActive += 1;
		}

		@Override
		public String toJsonString() {
			return """
					{
						"last_message": ["%s", "%s"],
						"message_count": %d,
						"minutes_active": %d
					}
					""".formatted(lastMessage.getId(), messageCount, minutesActive);
		}
	}

	private final Map<String, Map<String, ActivityData>> activityPerMemberPerGuild = new HashMap<>();
	private final Map<String, String> previousMessageContentPerUser = new HashMap<>();

	@SuppressWarnings("unchecked")
	private void readData() {
		try {
			Sj.parseObject(Util.readFile("atguilds.json")).entrySet().forEach(
				entry -> {
					final var guildId = entry.getKey();
					final var memberIdToObject = (Map<String, Map<String, Object>>) entry.getValue();
					final var activityPerMember = new HashMap<String, ActivityData>();
					for (final var e : memberIdToObject.entrySet()) {
						final var obj = new SjObject(e.getValue());
						activityPerMember.put(e.getKey(), new ActivityData(obj));
					}
					activityPerMemberPerGuild.put(guildId, activityPerMember);
				}
			);
		} catch (Exception e) {}
	}

	private ActivityTrackerBot(String token) {
		super(token);

		if (Util.fileExists("atguilds.json")) readData();

		//setCommands().thenRun(() -> System.out.println("Commands set!")).exceptionally(Util::printStackTrace);
		
		final var shutdownHook = new Thread(() -> Util.writeFile("atguilds.json", Sj.writePretty(activityPerMemberPerGuild)));
		Runtime.getRuntime().addShutdownHook(shutdownHook);

		gateway.connectAndIdentify(
			GatewayIntent.GUILDS,
			GatewayIntent.GUILD_MESSAGES
		);
	}

	@Override
	protected void onReady() {
		System.out.println("Logged in as " + user.getTag() + '!');
		guilds.cache.keySet().forEach(id -> {
			if (!activityPerMemberPerGuild.containsKey(id))
				activityPerMemberPerGuild.put(id, new HashMap<>());
		});
	}

	@Override
	protected void onInteractionCreate(Interaction i) {
		if (!(i instanceof final ChatInputInteraction interaction)) return;
		switch (interaction.commandName) {
			case "view_activity" -> {
				final var member = interaction.options.getMember("member").join();

				var activityPerMember = activityPerMemberPerGuild.get(interaction.guild.getId());

				if (activityPerMember == null) {
					activityPerMember = new HashMap<String, ActivityData>();
					activityPerMemberPerGuild.put(interaction.guild.getId(), activityPerMember);
				}

				if (activityPerMember.size() == 0) {
					interaction.reply("Your server has no recorded activity!");
					return;
				}

				final var embed = new Embed();
				
				if (member == null) {
					final var membersStr = new StringBuilder();
					final var activityNumsStr = new StringBuilder();
					for (final var entry : activityPerMember.entrySet()) {
						membersStr.append("<@").append(entry.getKey()).append(">\n");
						activityNumsStr.append(entry.getValue()).append('\n');
					}
					embed.title = "Activity per member";
					embed.addField("Member", membersStr.toString(), true);
					embed.addField("Activity", activityNumsStr.toString(), true);
				} else {
					embed.title = "Activity for <@" + member.getId() + '>';
					embed.description = activityPerMember.get(member.getId()) + " messages sent";
				}

				interaction.reply(embed);
			}
		}
	}

	@Override
	protected void onMessageCreate(Message message) {
		if (!message.inGuild) return;
		if (message.author.isBot) return;

		final var authorId = message.author.getId();
		final var guildId = message.guild.getId();
		final var content = message.getContent();

		final var previousMessageContent = previousMessageContentPerUser.get(authorId);
		if (content.equals(previousMessageContent)) return;
		previousMessageContentPerUser.put(authorId, content);

		var activityPerMember = activityPerMemberPerGuild.get(message.guild.getId());

		if (activityPerMember == null) {
			activityPerMember = new HashMap<String, ActivityData>();
			activityPerMemberPerGuild.put(guildId, activityPerMember);
		}

		var activity = activityPerMember.get(authorId);

		if (activity == null) {
			activity = new ActivityData(message);
		}

		activity.messageCount += 1;

		if (Instant.now().minusSeconds(message.createdInstant.getEpochSecond()).getEpochSecond() >= 60) {
			activity.minutesActive += 1;
		}

		activityPerMember.put(authorId, activity);
	}

	private CompletableFuture<Void> setCommands() {
		final var viewActivity = new ApplicationCommand.Payload();
		viewActivity.name = "view_activity";
		viewActivity.description = "View activity stats for all members, or a certain member.";

		final var viewActivityMember = new ApplicationCommandOption.NonSubcommandPayload();
		viewActivityMember.name = "member";
		viewActivityMember.description = "View activity stats for a certain member.";
		viewActivityMember.type = ApplicationCommandOption.Type.USER;

		viewActivity.options = List.of(viewActivityMember);

		final var commands = List.of(viewActivity);
		final Function<Guild, CompletableFuture<Void>> setGuildCommands = g -> g.commands.set(commands);

		return guilds.get("1131342149301055488").thenCompose(setGuildCommands);
	}

	public static void main(String[] args) {
		new ActivityTrackerBot(Util.readFile("tokens/activity-tracker"));
	}
}
