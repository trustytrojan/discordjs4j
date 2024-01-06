import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.ApplicationCommand;
import discord.resources.Embed;
import discord.resources.Message;
import discord.resources.User;
import discord.resources.channels.MessageChannel;
import discord.resources.guilds.Guild;
import discord.structures.ApplicationCommandOption;
import discord.structures.interactions.ChatInputInteraction;
import discord.structures.interactions.Interaction;
import discord.util.Util;
import sj.Sj;
import sj.SjObject;
import sj.SjSerializable;

public class ActivityTrackerBot extends BotDiscordClient {
	private static final String DATA_FILENAME = "at.json";
	private static final String TEST_GUILD_ID = "1131342149301055488";

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
			lastMessage = channel.getMessageManager().get(lastMessageArray.get(1)).join();
			messageCount = data.getLong("message_count");
			minutesActive = data.getLong("minutes_active");
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
			Sj.parseObject(Util.readFile(DATA_FILENAME)).entrySet().forEach(entry -> {
				final var guildId = entry.getKey();
				final var memberIdToObject = (Map<String, Map<String, Object>>) entry.getValue();
				final var activityPerMember = new HashMap<String, ActivityData>();
				for (final var e : memberIdToObject.entrySet()) {
					final var obj = new SjObject(e.getValue());
					activityPerMember.put(e.getKey(), new ActivityData(obj));
				}
				activityPerMemberPerGuild.put(guildId, activityPerMember);
			});
		} catch (Exception e) {
		}
	}

	private ActivityTrackerBot(String token) {
		super(token, false);

		if (Util.fileExists("atguilds.json"))
			readData();

		setCommands().thenRun(() -> System.out.println("Commands set!")).exceptionally(Util::printStackTrace);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Util.writeFile(DATA_FILENAME, Sj.writePretty(activityPerMemberPerGuild));
			gateway.close();
		}));

		gateway.connectAndIdentify(
				GatewayIntent.GUILDS,
				GatewayIntent.GUILD_MESSAGES);
	}

	@Override
	protected void onReady() {
		System.out.println("Logged in as " + currentUser.getTag() + '!');
		guilds.cache.keySet().forEach(guildId -> {
			if (!activityPerMemberPerGuild.containsKey(guildId))
				activityPerMemberPerGuild.put(guildId, new HashMap<>());
		});
	}

	@Override
	protected void onInteractionCreate(Interaction i) {
		if (!(i instanceof final ChatInputInteraction interaction))
			return;
		switch (interaction.commandName) {
			case "view_activity" -> {
				final var guild = interaction.getGuildAsync().join();

				var activityPerMember = activityPerMemberPerGuild.get(guild.getId());

				if (activityPerMember == null) {
					activityPerMember = new HashMap<String, ActivityData>();
					activityPerMemberPerGuild.put(guild.getId(), activityPerMember);
				}

				if (activityPerMember.size() == 0) {
					interaction.reply("Your server has no recorded activity!");
					return;
				}

				final var embed = new Embed();
				final var member = interaction.options.getMemberAsync("member").join();

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
		final var resources = Util.awaitResources(message.getGuild(), message.getAuthor());
		final var guild = (Guild) resources[0];
		final var author = (User) resources[1];

		if (guild == null || author.isBot())
			return;

		final var authorId = author.getId();
		final var guildId = guild.getId();
		final var content = message.getContent();

		final var previousMessageContent = previousMessageContentPerUser.get(authorId);
		if (content.equals(previousMessageContent))
			return;
		previousMessageContentPerUser.put(authorId, content);

		var activityPerMember = activityPerMemberPerGuild.get(guildId);

		if (activityPerMember == null) {
			activityPerMember = new HashMap<String, ActivityData>();
			activityPerMemberPerGuild.put(guildId, activityPerMember);
		}

		var activity = activityPerMember.get(authorId);

		if (activity == null) {
			activity = new ActivityData(message);
		}

		activity.messageCount += 1;

		if (Instant.now().minusSeconds(message.getCreatedInstant().getEpochSecond()).getEpochSecond() >= 60) {
			activity.minutesActive += 1;
		}

		activityPerMember.put(authorId, activity);
	}

	// @SuppressWarnings("unused")
	private CompletableFuture<Void> setCommands() {
		final var viewActivity = new ApplicationCommand.ChatInputPayload("view_activity", "View activity stats for all members, or a certain member.");

		viewActivity.options = List.of(
				new ApplicationCommandOption.NonSubcommandPayload(
						ApplicationCommandOption.Type.USER,
						"member",
						"View activity stats for a certain member."));

		final var commands = List.of(viewActivity);
		final Function<Guild, CompletableFuture<Void>> setGuildCommands = g -> g.commands.set(commands);

		return guilds.get(TEST_GUILD_ID).thenCompose(setGuildCommands);
	}

	public static void main(String[] args) {
		new ActivityTrackerBot(Util.readFile("tokens/at"));
	}
}