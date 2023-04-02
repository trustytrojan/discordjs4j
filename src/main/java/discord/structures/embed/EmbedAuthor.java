package discord.structures.embed;

import org.json.simple.JSONAware;

import simple_json.JSONObject;

record EmbedAuthor(String name, String iconURL, String url) implements JSONAware {

	@Override
	public String toJSONString() {
		final var obj = new JSONObject();

		obj.put("name", name);

		if (url != null) {
			obj.put("url", url);
		}

		if (iconURL != null) {
			obj.put("icon_url", iconURL);
		}

		return obj.toString();
	}

}
