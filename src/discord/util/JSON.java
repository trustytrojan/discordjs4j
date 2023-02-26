package discord.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public final class JSON {

	public static record ObjectEntry(String key, Object value) {}

	public static List<BetterJSONObject> parseObjectArrayFromFile(String filename) throws IOException, ParseException {
		return parseObjectArray(Util.readFile(filename));
	}

	public static BetterJSONObject parseObject(String s) throws ParseException {
		return new BetterJSONObject((JSONObject)JSONValue.parseWithException(s));
	}

	public static JSONArray parseArray(String s) throws ParseException {
		return (JSONArray)JSONValue.parseWithException(s);
	}

	@SuppressWarnings("unchecked")
	public static List<BetterJSONObject> parseObjectArray(String s) throws ParseException {
		final var better_objs = new ArrayList<BetterJSONObject>();
		for (final var obj : (List<JSONObject>)parseArray(s))
			better_objs.add(new BetterJSONObject(obj));
		return better_objs;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject buildObject(ObjectEntry ...entries) {
		final var obj = new JSONObject();
		for (final var entry : entries)
			obj.put(entry.key(), entry.value());
		return obj;
	}

	public static ObjectEntry objectEntry(String key, Object value) {
		return new ObjectEntry(key, value);
	}

	@SuppressWarnings("unchecked")
	public static JSONArray buildArray(List<? extends JSONable> objs) {
		final var arr = new JSONArray();
		for (final var obj : objs)
			arr.add(obj.toJSONObject());
		return arr;
	}

}
