package discord.structures.components;

import sj.SjObject;

public class LinkButton extends Button {
	public final String url;

	public LinkButton(final SjObject data) {
		super(data);
		url = data.getString("url");
	}

	public LinkButton(final String url) {
		super(Style.LINK);
		this.url = url;
	}

	@Override
	public String toJsonString() {
		final var obj = toJSONObject();
		obj.put("url", url);
		return obj.toJsonString();
	}
}
