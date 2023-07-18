package discord.resources;

import discord.client.DiscordClient;
import sj.SjObject;

public class Relationship extends AbstractDiscordResource {
	public static enum Type { NONE, FRIEND, BLOCKED, PENDING_INCOMING, PENDING_OUTGOING, IMPLICIT }

	private final String apiPath = "/users/@me/relationships/" + id;

	public Relationship(DiscordClient client, SjObject data) {
		super(client, data);
	}

	public Type type() {
		return Type.values()[data.getInteger("type")];
	}

	public User user() {
		return new User(client, data.getObject("user"));
	}

	@Override
	public String apiPath() {
		return apiPath;
	}
}
