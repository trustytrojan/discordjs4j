package discord.resources.channels;

import discord.client.DiscordClient;
import sj.SjObject;

public class VoiceChannel extends AbstractGuildChannel {
	public static class Payload extends GuildChannel.Payload {
		public boolean nsfw;
		public Integer bitrate;
		public Integer userLimit;
		public String parentId;
		public String rtcRegion;
		public VideoQualityMode videoQualityMode;

		public Payload(String name) {
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

		public final short value;

		private VideoQualityMode(int value) {
			this.value = (short) value;
		}
	}

	public VoiceChannel(DiscordClient client, SjObject data) {
		super(client, data);
	}
}
