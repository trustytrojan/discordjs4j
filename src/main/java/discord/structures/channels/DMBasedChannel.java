package discord.structures.channels;

public interface DMBasedChannel extends TextBasedChannel {
    
    default String url() {
        return "https://discord.com/channels/@me/" + id();
    }

}
