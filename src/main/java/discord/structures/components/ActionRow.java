package discord.structures.components;

import java.util.Arrays;
import java.util.List;

import simple_json.SjObject;

public class ActionRow extends MessageComponent {
	public final List<MessageComponent> components;

	public ActionRow(final SjObject data) {
		super(data);
		components = data.getObjectArray("components").stream()
			.map((final var rawComponent) -> MessageComponent.construct(rawComponent))
			.toList();
	}

	public ActionRow(final MessageComponent... components) {
		super(Type.ACTION_ROW);
		this.components = Arrays.asList(components);
	}

	@Override
	public String toJSONString() {
		final var obj = toJSONObject();
		obj.put("components", components);
		return obj.toJSONString();
	}
}
