package discord.structures.embed;

import org.json.simple.JSONObject;

import discord.util.JSONable;

public record EmbedFooter(String text, String icon_url) implements JSONable {

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		final var obj = new JSONObject();
		obj.put("text", text);
		if (icon_url != null) obj.put("icon_url", icon_url);
		return obj;
	}

}
