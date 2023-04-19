package discord.structures;

public class InteractionReplyMessagePayload extends Message.Payload {
	public boolean ephemeral;

	@Override
	public String toJSONString() {
		final var obj = toJSONObject();
		if (ephemeral)
			obj.put("ephemeral", Boolean.TRUE);
		return obj.toString();
	}
}
