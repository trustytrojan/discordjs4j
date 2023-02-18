package discord.util;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public interface JSONable extends JSONAware {

	@Override
	default String toJSONString() { return toJSONObject().toJSONString(); }

	public JSONObject toJSONObject();
	
}
