import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import discord.client.BotDiscordClient;
import discord.enums.GatewayIntent;
import discord.resources.ApplicationCommand;
import discord.resources.ApplicationCommandOption;
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
			Sj.parseObject(Util.readFile("atguilds.json")).entrySet().forEach(
				entry -> activityPerMemberPerGuild.put(entry.getKey(), (HashMap<String, Long>) entry.getValue())
			);
		}

		//setCommands().thenRun(() -> System.out.println("Commands set!")).exceptionally(Util::printStackTrace);
		
		final var shutdownHook = new Thread(() -> Util.writeFile("atguilds.json", Sj.writePretty(activityPerMemberPerGuild)));
		Runtime.getRuntime().addShutdownHook(shutdownHook);

		gateway.connectAndIdentify(GatewayIntent.GUILDS, GatewayIntent.GUILD_MEMBERS);
	}

	@Override
	protected void onReady() {
		System.out.println("Logged in as " + user.tag() + '!');
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
				interaction.reply("n");
			}
		}
	}

	@Override
	protected void onMessageCreate(Message message) {
		if (!message.inGuild) return;
		final var authorId = message.author.id();
		final var content = message.content();
		final var previousMessageContent = previousMessageContentPerUser.get(authorId);
		if (content.equals(previousMessageContent)) return;
		previousMessageContentPerUser.put(authorId, content);
		final var activityPerMember = activityPerMemberPerGuild.get(message.guild.id());
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
