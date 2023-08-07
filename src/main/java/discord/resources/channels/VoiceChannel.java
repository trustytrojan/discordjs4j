package discord.resources.channels;

import discord.client.DiscordClient;
import discord.managers.MessageManager;
import discord.resources.AbstractGuildResource;
import discord.resources.guilds.Guild;
import sj.SjObject;

public class VoiceChannel extends AbstractGuildResource implements GuildChannel, MessageChannel {
	public static class Payload extends GuildChannel.Payload {
		public boolean nsfw;
		public Integer bitrate;
		public Integer userLimit;
		public String parentId;
		public String rtcRegion;
		public VideoQualityMode videoQualityMode;

		public Payload(final String name) {
			super(name);
		}

		@Override
		public String toJsonString() {
			final var obj = toSjObject();
			obj.put("type", Channel.Type.GUILD_VOICE.value);
			if (nsfw)
				obj.put("nsfw", Boolean.TRUE);
			if (bitrate != null)
				obj.put("bitrate", bitrate);
			if (userLimit != null)
				obj.put("user_limit", userLimit);
			if (parentId != null)
				obj.put("parent_id", parentId);
			if (rtcRegion != null)
				obj.put("rtc_region", rtcRegion);
			if (videoQualityMode != null)
				obj.put("video_quality_mode", videoQualityMode.value);
			return obj.toString();
		}
	}

	public static enum VideoQualityMode {
		AUTO(1),
		FULL(2);

		public static final VideoQualityMode[] LOOKUP_TABLE = { null, AUTO, FULL };

		public final int value;

		private VideoQualityMode(final int value) {
			this.value = value;
		}
	}

	private final MessageManager messageManager;

	public VoiceChannel(DiscordClient client, Guild guild, SjObject data) {
		super(client, guild, data);
		messageManager = new MessageManager(client, this);
	}

	public VideoQualityMode videoQualityMode() {
		return VideoQualityMode.LOOKUP_TABLE[data.getInteger("video_quality_mode")];
	}

	public int bitrate() {
		return data.getInteger("bitrate");
	}

	public int userLimit() {
		return data.getInteger("user_limit");
	}

	public String rtcRegion() {
		return data.getString("rtc_region");
	}

	@Override
	public MessageManager getMessageManager() {
		return messageManager;
	}
}
