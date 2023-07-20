package discord.resources;

import java.util.concurrent.CompletableFuture;

import discord.client.DiscordClient;
import discord.util.BitFlagSet;
import discord.util.Util;
import sj.SjObject;
import sj.SjSerializable;

public final class CurrentUser extends User {
	public static enum PremiumType { NONE, NITRO_CLASSIC, NITRO, NITRO_BASIC }

	public static class Payload implements SjSerializable {
		public String username;
		public String globalName;
		public String aboutMe;

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			if (username != null) obj.put("username", username);
			if (globalName != null) obj.put("global_name", globalName);
			if (aboutMe != null) obj.put("bio", aboutMe);
			return obj.toJsonString();
		}
	}

	public CurrentUser(final DiscordClient client, final SjObject data) {
		super(client, data);
	}

	public CompletableFuture<Void> edit(Payload payload) {
		return client.api.patch("/users/@me", payload.toJsonString()).thenRun(Util.NO_OP);
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
		return PremiumType.values()[data.getInteger("premium_type")];
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

	public BitFlagSet<Flag> flags() {
		return new BitFlagSet<>(data.getInteger("flags"));
	}
}
