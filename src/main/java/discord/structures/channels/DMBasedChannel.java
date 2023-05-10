package discord.structures.channels;

public interface DMBasedChannel extends TextBasedChannel {
    @Override
    default String url() {
        return "https://discord.com/channels/@me/" + id();
    }
}
