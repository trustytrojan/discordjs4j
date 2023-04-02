package discord.structures.embed;

import org.json.simple.JSONAware;

import simple_json.JSONObject;

record EmbedFooter(String text, String iconURL) implements JSONAware {

	@Override
	public String toJSONString() {
		final var obj = new JSONObject();

		obj.put("text", text);

		if (iconURL != null) {
			obj.put("icon_url", iconURL);
		}

		return obj.toString();
	}

}
