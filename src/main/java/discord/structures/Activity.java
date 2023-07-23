package discord.structures;

import sj.SjObject;
import sj.SjSerializable;

/**
 * https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-structure
 */
public class Activity implements SjSerializable {
	public static enum Type { PLAYING, STREAMING, LISTENING, WATCHING, CUSTOM, COMPETING }

	public final String name;
	public final Type type;
	public String url;

	public Activity(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	// for subclasses to call
	public SjObject toSjObject() {
		final var obj = new SjObject();
		obj.put("name", name);
		obj.put("type", type.ordinal());
		if (url != null)
			obj.put("url", url);
		return obj;
	}

	@Override
	public String toJsonString() {
		return toSjObject().toJsonString();
	}
}
