package discord.structures;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONAware;

import discord.util.Util;
import simple_json.JSONObject;

public class Embed implements JSONAware {

	private static record Author(String name, String iconURL, String url) implements JSONAware {
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

	private static record Field(String name, String value, boolean inline) implements JSONAware {
		@Override
		public String toJSONString() {
			final var obj = new JSONObject();
			
			obj.put("name", name);
			obj.put("value", value);
	
			if (inline) {
				obj.put("inline", inline);
			}
	
			return obj.toString();
		}
	}
	
	private static record Footer(String text, String iconURL) implements JSONAware {
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
		color = Util.resolveColor(hexColor);
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
	public String toJSONString() {
		final var obj = new JSONObject();

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
			final var image = new JSONObject();
			image.put("url", this.image);
			obj.put("image", image);
		}

		if (thumbnail != null) {
			final var thumbnail = new JSONObject();
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
