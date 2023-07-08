package discord.structures;

import java.util.List;

import discord.client.DiscordClient;
import sj.SjObject;

public final class ClientUser extends User {
	public static enum PremiumType { NONE, NITRO_CLASSIC, NITRO, NITRO_BASIC }

	public ClientUser(final DiscordClient client, final SjObject data) {
		super(client, data);
	}

	public String bio() {
		return data.getString("bio");
	}

	public String locale() {
		return data.getString("locale");
	}

	public boolean nsfwAllowed() {
		return data.getBooleanDefaultFalse("nsfw_allowed");
	}

	public boolean mfaEnabled() {
		return data.getBooleanDefaultFalse("mfa_enabled");
	}

	public PremiumType premiumType() {
		return PremiumType.values()[data.getLong("premium_type").intValue()];
	}

	public String email() {
		return data.getString("email");
	}

	public boolean verified() {
		return data.getBooleanDefaultFalse("verified");
	}

	public String phone() {
		return data.getString("phone");
	}

	public List<Flag> flags() {
		return computeFlags(data.getLong("flags").intValue());
	}
}
