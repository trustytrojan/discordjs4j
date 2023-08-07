package discord.resources.channels;

public interface NonGuildChannel extends Channel {
	@Override
	default String getUrl() {
		return "https://discord.com/channels/@me/" + getId();
	}
}
