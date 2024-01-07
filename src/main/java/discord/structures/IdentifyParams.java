package discord.structures;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import discord.enums.GatewayIntent;
import sj.SjObject;
import sj.SjSerializable;

/**
 * https://discord.com/developers/docs/topics/gateway-events#identify-identify-structure
 * <p>
 * The {@code token} field should have been given to a {@code DiscordClient}
 * instance.
 * It will handle your token properly, so
 */
public class IdentifyParams implements SjSerializable {
	public static final Map<String, String> DEFAULT_CONNECTION_PROPERTIES =
		Map.of(
			"os", System.getProperty("os.name"),
			"browser", "discordjs4j",
			"device", "discordjs4j"
		);

	private String token;
	public String os = System.getProperty("os.name");
	public String browser = "discordjs4j";
	public String device = "discordjs4j";
	public boolean compress;
	public short largeThreshold;
	public Integer shardId, numShards;
	private final GatewayIntent[] intents;
	public UpdatePresence presence;

	public IdentifyParams(String token, GatewayIntent... intents) {
		this.token = Objects.requireNonNull(token);
		for (final var intent : intents)
			Objects.requireNonNull(intent);
		this.intents = intents;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toJsonString() {
		final var obj = new SjObject();
		obj.put("token", token);
		obj.put("properties", Map.of("os", os, "browser", browser, "device", device));
		if (compress)
			obj.put("compress", true);
		if (largeThreshold > 0)
			obj.put("large_threshold", largeThreshold);
		if (shardId > 0 && numShards > 0)
			obj.put("shard", List.of(shardId, numShards));
		if (presence != null)
			obj.put("presence", presence);
		obj.put("intents", GatewayIntent.sum(intents));
		return obj.toJsonString();
	}
}
