import java.util.HashMap;
import java.util.List;
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
	private final HashMap<String, HashMap<String, Long>> activityPerMemberPerGuild = new HashMap<>();
	private final HashMap<String, String> previousMessageContentPerUser = new HashMap<>();

	@SuppressWarnings("unchecked")
	private ActivityTrackerBot() {
		super(Util.readFile("tokens/activity-tracker"));

		if (Util.fileExists("atguilds.json")) {
			try {
				Sj.parseObject(Util.readFile("atguilds.json")).entrySet().forEach(
					entry -> activityPerMemberPerGuild.put(entry.getKey(), (HashMap<String, Long>) entry.getValue())
				);
			} catch (Exception e) {}
		}

		//setCommands().thenRun(() -> System.out.println("Commands set!")).exceptionally(Util::printStackTrace);
		
		final var shutdownHook = new Thread(() -> Util.writeFile("atguilds.json", Sj.writePretty(activityPerMemberPerGuild)));
		Runtime.getRuntime().addShutdownHook(shutdownHook);

		gateway.connectAndIdentify(GatewayIntent.GUILDS, GatewayIntent.GUILD_MEMBERS);
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

				final var embed = new Embed();
				if (member == null) {
					final var membersStr = new StringBuilder();
					final var activityNumsStr = new StringBuilder();
					for (final var entry : activityPerMember.entrySet()) {
						membersStr.append(entry.getKey());
						activityNumsStr.append(entry.getValue());
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

		activityPerMember.put(authorId, activityPerMember.get(authorId) + 1);
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
		new ActivityTrackerBot();
	}
}
