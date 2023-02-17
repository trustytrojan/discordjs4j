package discord.structures.commands;

import org.json.simple.JSONObject;

import discord.util.BetterJSONObject;
import discord.util.JSONable;

/**
 * value should be one of String, Long, or Double
 */
public record ApplicationCommandOptionChoice(String name, Object value) implements JSONable {

  @Override
  public String toJSONString() {
    return toJSONObject().toJSONString();
  }

  @Override
  public JSONObject toJSONObject() {
    final var obj = new BetterJSONObject();
    obj.put("name", name);
    obj.put("value", value);
    return obj.innerObject;
  }

}
