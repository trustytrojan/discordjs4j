package discord.structures;

import java.util.List;

import sj.SjObject;
import sj.SjSerializable;

/**
 * https://discord.com/developers/docs/topics/gateway-events#update-presence
 * <p>
 * Sent by the client to indicate a presence or status update.
 */
public class UpdatePresence implements SjSerializable {
	public static enum Status { ONLINE, DND, IDLE, INVISIBLE, OFFLINE }

	public Long since;
	public final List<Activity> activities;
	public final Status status;
	public final boolean afk;

	public UpdatePresence(
		final List<Activity> activities,
		final Status status,
		final boolean afk
	) {
		this.activities = activities;
		this.status = status;
		this.afk = afk;
	}

	@Override
	public String toJsonString() {
		final var obj = new SjObject();
		obj.put("since", since);
		obj.put("activities", activities);
		obj.put("status", status.toString().toLowerCase());
		obj.put("afk", afk);
		return obj.toJsonString();
	}
}
