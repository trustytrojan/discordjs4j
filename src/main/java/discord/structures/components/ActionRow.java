package discord.structures.components;

import java.util.Arrays;
import java.util.List;

import sj.SjObject;

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
	public String toJsonString() {
		final var obj = toJsonObject();
		obj.put("components", components);
		return obj.toJsonString();
	}
}
