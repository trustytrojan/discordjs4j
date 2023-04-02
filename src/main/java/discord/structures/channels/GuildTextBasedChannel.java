package discord.structures.channels;

import java.util.concurrent.CompletableFuture;

public interface GuildTextBasedChannel extends GuildChannel, TextBasedChannel {

    default CompletableFuture<GuildChannel> edit(Payload payload) {
        return guild().channels.edit(id(), payload);
    }
    
    public static class Payload extends GuildChannel.Payload {
        public Channel.Type type;
		public String topic;
		public boolean nsfw;
		public Short rateLimitPerUser;
		public String parentId;

        @Override
        public String toJSONString() {
            final var obj = toJSONObject();

            if (type != null) {
                obj.put("type", type.value);
            }

            if (topic != null) {
                obj.put("topic", topic);
            }

            if (nsfw) {
                obj.put("nsfw", true);
            }

            if (rateLimitPerUser != null) {
                obj.put("rate_limit_per_user", rateLimitPerUser);
            }

            if (parentId != null) {
                obj.put("parent_id", parentId);
            }

            return obj.toString();
        }
    }

}
