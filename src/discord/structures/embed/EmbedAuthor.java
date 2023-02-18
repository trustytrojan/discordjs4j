package discord.structures.embed;

import org.json.simple.JSONObject;

import discord.util.BetterJSONObject;
import discord.util.JSONable;

public record EmbedAuthor(
	String name,
	String url,
	String icon_url
) implements JSONable {

	@Override
	public JSONObject toJSONObject() {
		final var obj = new BetterJSONObject();
		obj.put("name", name);
		if (url != null) obj.put("url", url);
		if (icon_url != null) obj.put("icon_url", icon_url);
		return obj.innerObject;
	}

}
