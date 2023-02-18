package discord.util;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BetterJSONObject {

	public final JSONObject innerObject;

	public BetterJSONObject(JSONObject data) {
		innerObject = data;
	}

	public BetterJSONObject() {
		this(new JSONObject());
	}

	@Override
	public String toString() {
		return innerObject.toString();
	}

	@SuppressWarnings("unchecked")
	public void put(String key, Object value) {
		innerObject.put(key, value);
	}

	public Object get(String key) {
		return innerObject.get(key);
	}

	// typecasting shorthand methods, can throw exceptions!
	public String getString(String key) {
		return (String)get(key);
	}

	public Long getLong(String key) {
		return (Long)get(key);
	}

	public Double getDouble(String key) {
		return (Double)get(key);
	}

	public Boolean getBoolean(String key) {
		return (Boolean)get(key);
	}

	public BetterJSONObject getObject(String key) {
		return new BetterJSONObject((JSONObject)get(key));
	}

	public JSONArray getArray(String key) {
		return (JSONArray)get(key);
	}

	@SuppressWarnings("unchecked")
	public List<String> getStringArray(String key) {
		return (List<String>)getArray(key);
	}

	public List<BetterJSONObject> getObjectArray(String key) {
		final var objects = new ArrayList<BetterJSONObject>();
		for (final var obj : getArray(key))
			objects.add(new BetterJSONObject((JSONObject)obj));
		return objects;
	}

}
