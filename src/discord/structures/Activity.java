package discord.structures;

import java.util.Date;

import discord.enums.ActivityType;

public record Activity(
	ActivityAssets assets,
	String name,
	Date created_at,
	String details,
	String state,
	String id,
	ActivityType type,
	String application_id
) {}
