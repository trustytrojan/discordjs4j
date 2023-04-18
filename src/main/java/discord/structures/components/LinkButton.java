package discord.structures.components;

import simple_json.JSONObject;

public class LinkButton extends Button {
	public final String url;

	public LinkButton(final JSONObject data) {
		super(data);
		url = data.getString("url");
	}

	public LinkButton(final String url) {
		super(Style.LINK);
		this.url = url;
	}

	@Override
	public String toJSONString() {
		final var obj = toJSONObject();
		obj.put("url", url);
		return obj.toJSONString();
	}
}
