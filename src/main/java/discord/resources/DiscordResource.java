package discord.resources;

import discord.client.DiscordClient;
import sj.SjObject;

public interface DiscordResource {
	/**
	 * @return The {@link DiscordClient} that created this resource.
	 */
	DiscordClient getClient();

	/**
	 * @return The {@link SjObject} that contains the data for this resource.
	 */
	SjObject getData();

	/**
	 * Patches this resource's data with fresh data from Discord.
	 * 
	 * @param data Fresh data from Discord.
	 */
	void setData(SjObject data);

	/**
	 * @return This resource's ID.
	 */
	String getId();

	/**
	 * @return Whether this resource has been deleted from Discord.
	 */
	boolean wasDeleted();

	/**
	 * Set's this resource's {@code deleted} flag to {@code true}.
	 */
	void setDeleted();
}
