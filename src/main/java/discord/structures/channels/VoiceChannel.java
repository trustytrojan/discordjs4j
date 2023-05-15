package discord.structures.channels;

import discord.client.DiscordClient;
import discord.structures.Guild;
import simple_json.SjObject;

public class VoiceChannel implements GuildChannel {
	public static enum VideoQualityMode {
		AUTO(1),
		FULL(2);

		public static VideoQualityMode resolve(final int value) {
			for (final var x : VideoQualityMode.values())
				if (x.value == value)
					return x;
			return null;
		}

		public int value;

		private VideoQualityMode(int value) {
			this.value = value;
		}
	}
	
	private final DiscordClient client;
	private SjObject data;

	private final Guild guild;

	public VoiceChannel(final DiscordClient client, final SjObject data) {
		this.client = client;
		setData(data);
		guild = client.guilds.fetch(guildId()).join();
	}

	@Override
	public SjObject getData() {
		return data;
	}

	@Override
	public void setData(final SjObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}
	
	@Override
	public Guild guild() {
		return guild;
	}

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
		public String toJSONString() {
			final var obj = toJSONObject();

			if (nsfw) {
				obj.put("nsfw", Boolean.TRUE);
			}

			if (bitrate != null) {
				obj.put("bitrate", bitrate);
			}

			if (userLimit != null) {
				obj.put("user_limit", userLimit);
			}

			if (parentId != null) {
				obj.put("parent_id", parentId);
			}

			if (rtcRegion != null) {
				obj.put("rtc_region", rtcRegion);
			}

			if (videoQualityMode != null) {
				obj.put("video_quality_mode", videoQualityMode.value);
			}

			return obj.toString();
		}
	}

}
