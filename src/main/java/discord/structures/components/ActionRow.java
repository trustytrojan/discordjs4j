package discord.structures.components;

import java.util.ArrayList;
import java.util.List;

import simple_json.JSONObject;

public class ActionRow extends MessageComponent {
	public List<MessageComponent> components;

	public ActionRow(final JSONObject data) {
		super(data);
		components = data.getObjectArray("components").stream()
			.map((final var rawComponent) -> MessageComponent.construct(rawComponent))
			.toList();
	}

	public ActionRow() {
		super(Type.ACTION_ROW);
		components = new ArrayList<>();
	}

	@Override
	public String toJSONString() {
		final var obj = toJSONObject();
		obj.put("components", components);
		return obj.toJSONString();
	}
}
