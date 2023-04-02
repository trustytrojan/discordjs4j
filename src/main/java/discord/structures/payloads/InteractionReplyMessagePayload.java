package discord.structures.payloads;

public class InteractionReplyMessagePayload extends MessagePayload {

	private Boolean ephemeral;
	
	public void setEphemeral(boolean ephemeral) {
		this.ephemeral = ephemeral;
	}

	@Override
	public String toJSONString() {
		final var obj = toJSONObject();

		if (ephemeral != null) {
			obj.put("ephemeral", ephemeral);
		}

		return obj.toString();
	}

}
