package discord.structures;

import discord.util.BetterJSONObject;
import discord.client.DiscordClient;
import discord.util.CDN;

public class User implements DiscordObject {

	private final DiscordClient client;
	private BetterJSONObject data;

	public User(DiscordClient client, BetterJSONObject data) {
		this.client = client;
		this.data = data;
	}

	@Override
	public String toString() {
		return String.format("<@%s>", id());
	}

	public String avatarURL(int size, String extension) {
		return CDN.avatar(id(), avatar(), size, extension);
	}

	public String avatarURL(int size) {
		return avatarURL(size, null);
	}

	public String avatarURL(String extension) {
		return avatarURL(0, extension);
	}

	public String avatarURL() {
		return avatarURL(0, null);
	}

	public String username() {
		return data.getString("username");
	}

	public String discriminator() {
		return data.getString("discriminator");
	}

	public String tag() {
		return String.format("%s#%s", username(), discriminator());
	}

	public String avatar() {
		return data.getString("avatar");
	}

	public Boolean bot() {
		return data.getBoolean("bot");
	}

	public String banner() {
		return data.getString("banner");
	}

	// Not in API documentation, but this field can be present
	public String banner_color() {
		return data.getString("banner_color");
	}

	public Long accent_color() {
		return data.getLong("accent_color");
	}

	// This is a complicated one... do later
	// public UserFlags public_flags() {
	//   return 
	// }

	@Override
	public String api_path() {
		return String.format("/users/%s", id());
	}

	@Override
	public BetterJSONObject getData() {
		return data;
	}

	@Override
	public void setData(BetterJSONObject data) {
		this.data = data;
	}

	@Override
	public DiscordClient client() {
		return client;
	}

}
