package discord.structures;

import org.json.simple.JSONObject;

public class InteractionReplyMessagePayload extends MessagePayload {

	private Boolean ephemeral;
	
	public void setEphemeral(boolean ephemeral) {
		this.ephemeral = ephemeral;
	}

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		final var obj = super.toJSONObject();
		if (ephemeral != null)
			obj.put("ephemeral", ephemeral);
		return obj;
	}

}
