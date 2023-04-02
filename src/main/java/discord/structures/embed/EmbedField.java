package discord.structures.embed;

import org.json.simple.JSONAware;

import simple_json.JSONObject;

record EmbedField(String name, String value, boolean inline) implements JSONAware {

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
