package discord.structures.embed;

import java.util.ArrayList;
import java.util.List;  

import org.json.simple.JSONObject;

import discord.util.JSON;
import discord.util.JSONable;

public class Embed implements JSONable {

  private EmbedAuthor author;
  private String title;
  private String url;
  private String description;
  private List<EmbedField> fields = new ArrayList<>();
  private String image;
  private EmbedFooter footer;
  private String thumbnail;
  public int color;

  public void setAuthor(String name, String icon_url, String url) {
    author = new EmbedAuthor(name, url, icon_url);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setURL(String url) {
    this.url = url;
  }

  public void setImage(String url) {
    image = url;
  }

  public void setThumbnail(String url) {
    thumbnail = url;
  }

  public void setFooter(String text, String icon_url) {
    footer = new EmbedFooter(text, icon_url);
  }

  public void addField(String name, String value, boolean inline) {
    fields.add(new EmbedField(name, value, inline));
  }

  public void addField(String name, String value) {
    addField(name, value, false);
  }

  @SuppressWarnings("unchecked")
  public JSONObject toJSONObject() {
    final var obj = new JSONObject();

    if(title != null) {
      obj.put("title", title);
    }

    if(url != null) {
      obj.put("url", url);
    }

    if(description != null) {
      obj.put("description", description);
    }

    if(this.image != null) {
      final var image = new JSONObject();
      image.put("url", this.image);
      obj.put("image", image);
    }

    if(this.thumbnail != null) {
      final var thumbnail = new JSONObject();
      thumbnail.put("url", this.thumbnail);
      obj.put("thumbnail", thumbnail);
    }

    if(author != null) {
      obj.put("author", author.toJSONObject());
    }

    if(footer != null) {
      obj.put("footer", footer.toJSONObject());
    }

    if(fields != null) {
      obj.put("fields", JSON.buildArray(fields));
    }

    return obj;
  }

}
