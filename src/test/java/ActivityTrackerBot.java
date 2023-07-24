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
import discord.resources.guilds.Guild;
import discord.resources.interactions.ChatInputInteraction;
import discord.resources.interactions.Interaction;
import discord.util.Util;
import sj.Sj;

public class ActivityTrackerBot extends BotDiscordClient {
	private final Map<String, Map<String, Long>> activityPerMemberPerGuild = new HashMap<>();
	private final Map<String, String> previousMessageContentPerUser = new HashMap<>();

	@SuppressWarnings("unchecked")
	private void readData() {
		try {
			Sj.parseObject(Util.readFile("atguilds.json")).entrySet().forEach(
				entry -> activityPerMemberPerGuild.put(entry.getKey(), (HashMap<String, Long>) entry.getValue())
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
					activityPerMember = new HashMap<String, Long>();
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
			activityPerMember = new HashMap<String, Long>();
			activityPerMemberPerGuild.put(guildId, activityPerMember);
		}

		var activity = activityPerMember.get(authorId);

		if (activity == null) {
			activity = Long.valueOf(0);
		}

		activityPerMember.put(authorId, activity + 1);
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
