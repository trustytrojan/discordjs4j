package discord.structures.payloads;

import discord.structures.Message;

public class InteractionReplyMessagePayload extends Message.Payload {

	public Boolean ephemeral;
	
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
