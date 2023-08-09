package discord.structures.components;

import java.util.List;

import sj.SjObject;

public class ActionRow extends MessageComponent {
	public final List<MessageComponent> components;

	public ActionRow(final SjObject data) {
		super(data);
		if (type != MessageComponent.Type.ACTION_ROW)
			throw new IllegalArgumentException();
		components = data.getObjectArray("components").stream()
				.map((final var rawComponent) -> MessageComponent.construct(rawComponent))
				.toList();
	}

	public ActionRow(final MessageComponent... components) {
		super(Type.ACTION_ROW);
		for (final var component : components)
			if (component.type == MessageComponent.Type.ACTION_ROW)
				throw new IllegalArgumentException("Action rows cannot contain action rows");
		this.components = List.of(components);
	}

	@Override
	public String toJsonString() {
		final var obj = toJsonObject();
		obj.put("components", components);
		return obj.toJsonString();
	}
}
