package discord.structures.components;

import sj.SjObject;

public class LinkButton extends Button {
	public final String url;

	LinkButton(final SjObject data) {
		super(data);
		final var typeIsNotButton = (data.getInteger("type") != MessageComponent.Type.BUTTON.getValue());
		final var styleIsNotLink = (data.getInteger("style") != Button.Style.LINK.getValue());
		if (typeIsNotButton || styleIsNotLink)
			throw new IllegalArgumentException("Type is not BUTTON or style is not LINK");
		url = data.getString("url");
	}

	LinkButton(final String url) {
		super(Style.LINK);
		this.url = url;
	}

	@Override
	public String toJsonString() {
		final var obj = toJsonObject();
		obj.put("url", url);
		return obj.toJsonString();
	}
}
