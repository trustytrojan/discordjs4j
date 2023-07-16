package discord.resources;

import java.util.ArrayList;
import java.util.List;

import discord.util.Util;
import sj.SjObject;
import sj.SjSerializable;

public class Embed implements SjSerializable {

	private static record Author(String name, String iconURL, String url) implements SjSerializable {
		@Override
		public String toJsonString() {
			final var obj = new SjObject();
	
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

	private static record Field(String name, String value, boolean inline) implements SjSerializable {
		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			
			obj.put("name", name);
			obj.put("value", value);
	
			if (inline) {
				obj.put("inline", inline);
			}
	
			return obj.toString();
		}
	}
	
	private static record Footer(String text, String iconURL) implements SjSerializable {
		@Override
		public String toJsonString() {
			final var obj = new SjObject();
	
			obj.put("text", text);
	
			if (iconURL != null) {
				obj.put("icon_url", iconURL);
			}
	
			return obj.toString();
		}
	}
	
	private Author author;
	private Footer footer;
	private List<Field> fields = new ArrayList<>();

	public String title;
	public String url;
	public String description;
	public String image;
	public String thumbnail;
	public Integer color;
	
	public void setColor(String hexColor) throws IllegalArgumentException {
		color = Util.resolveHexColor(hexColor);
	}

	public void setAuthor(String name, String iconURL, String url) {
		author = new Author(name, iconURL, url);
	}

	public void setAuthor(String name, String iconURL) {
		author = new Author(name, iconURL, null);
	}

	public void setAuthor(String name) {
		author = new Author(name, null, null);
	}
	
	public void setFooter(String text, String iconURL) {
		footer = new Footer(text, iconURL);
	}

	public void setFooter(String text) {
		footer = new Footer(text, null);
	}

	public void addField(String name, String value, boolean inline) {
		fields.add(new Field(name, value, inline));
	}

	public void addField(String name, String value) {
		fields.add(new Field(name, value, false));
	}

	@Override
	public String toJsonString() {
		final var obj = new SjObject();

		if (title != null) {
			obj.put("title", title);
		}

		if (url != null) {
			obj.put("url", url);
		}

		if (description != null) {
			obj.put("description", description);
		}

		if (image != null) {
			final var image = new SjObject();
			image.put("url", this.image);
			obj.put("image", image);
		}

		if (thumbnail != null) {
			final var thumbnail = new SjObject();
			thumbnail.put("url", this.thumbnail);
			obj.put("thumbnail", thumbnail);
		}

		if (author != null) {
			obj.put("author", author);
		}

		if (footer != null) {
			obj.put("footer", footer);
		}

		if (fields.size() > 0) {
			obj.put("fields", fields);
		}

		if (color != null) {
			obj.put("color", color);
		}

		return obj.toString();
	}

}
