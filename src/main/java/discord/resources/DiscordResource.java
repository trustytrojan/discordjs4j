package discord.resources;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import discord.client.APIClient.JsonResponse;
import discord.client.DiscordClient;
import discord.util.Util;
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
	 * @throws IllegalArgumentException If the data does not match the structure of a Discord resource.
	 */
	void setData(SjObject data);

	/**
	 * @return This resource's ID.
	 */
	default String getId() {
		return getData().getString("id");
	}

	/**
	 * @return The {@link Instant} that this resource was created.
	 */
	default Instant getCreatedInstant() {
		return Util.Snowflake.toInstant(getId());
	}

	/**
	 * @return This resource's specific path on the Discord API.
	 */
	String getApiPath();

	/**
	 * @return Refreshes this DiscordResource's internal data with data fresh from
	 *         the Discord API.
	 */
	default CompletableFuture<Void> refreshData() {
		if (isDeleted())
			return CompletableFuture.failedFuture(new IllegalStateException("this resource has been marked as deleted!"));
		return getClient().api.get(getApiPath())
			.thenApply(JsonResponse::asObject)
			.thenAccept(this::setData);
	}

	/**
	 * @return Whether this resource has been marked as deleted from Discord.
	 */
	boolean isDeleted();

	/**
	 * Marks this resource as deleted from Discord.
	 */
	void markAsDeleted();
}
