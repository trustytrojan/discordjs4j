package discord.structures;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import discord.enums.GatewayEvent;
import discord.enums.GatewayOpcode;
import discord.util.BitFlagSet;
import discord.util.BitFlagSet.BitFlag;
import discord.util.MutableBitFlagSet;
import sj.SjObject;
import sj.SjSerializable;

/**
 * https://discord.com/developers/docs/topics/gateway-events#activity-object
 */
public class Activity implements SjSerializable {
	public static enum Type { PLAYING, STREAMING, LISTENING, WATCHING, CUSTOM, COMPETING }

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-timestamps
	 */
	public static class Timestamps implements SjSerializable {
		/**
		 * When the activity started. Can be {@code null}.
		 */
		public Instant start;

		/**
		 * When the activity ends. Can be {@code null}.
		 */
		public Instant end;

		private Timestamps(final SjObject data) {
			if (data.containsKey("start"))
				start = Instant.ofEpochMilli(data.getInteger("start"));
			if (data.containsKey("end"))
				end = Instant.ofEpochMilli(data.getInteger("end"));
		}

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			if (start != null)
				obj.put("start", start.toEpochMilli());
			if (end != null)
				obj.put("end", end.toEpochMilli());
			return obj.toJsonString();
		}
	}

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-emoji
	 */
	public static class Emoji implements SjSerializable {
		/**
		 * Name of the emoji
		 */
		public final String name;

		/**
		 * ID of the emoji. Can be {@code null}.
		 */
		public String id;

		/**
		 * Whether the emoji is animated
		 */
		public boolean animated;

		/**
		 * @param name Name of the emoji. Must be non-{@code null}.
		 */
		public Emoji(final String name) {
			this.name = Objects.requireNonNull(name);
		}

		private Emoji(final SjObject data) {
			name = Objects.requireNonNull(data.getString("name"));
			id = data.getString("id");
			animated = data.getBooleanDefaultFalse("animated");
		}

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			obj.put("name", name);
			if (id != null)
				obj.put("id", id);
			if (animated)
				obj.put("animated", true);
			return obj.toJsonString();
		}
	}

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-party
	 */
	public static class Party implements SjSerializable {
		/**
		 * ID of the party
		 */
		public String id;

		/**
		 * Used to show the party's current and maximum size
		 */
		public Integer currentSize, maxSize;

		private Party(final SjObject data) {
			id = data.getString("id");
			final var size = data.getArray("size");
			if (size != null) {
				currentSize = (Integer) size.get(0);
				maxSize = (Integer) size.get(1);
			}
		}

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			if (id != null)
				obj.put("id", id);
			if (currentSize != null && maxSize != null)
				obj.put("size", List.of(currentSize, maxSize));
			return obj.toJsonString();
		}
	}

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-assets
	 */
	public static class Assets implements SjSerializable {
		/**
		 * See <a href="https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-asset-image">Activity Asset Image</a>
		 */
		public String largeImage;
		
		/**
		 * Text displayed when hovering over the large image of the activity
		 */
		public String largeText;
		
		/**
		 * See <a href="https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-asset-image">Activity Asset Image</a>
		 */
		public String smallImage;
		
		/**
		 * Text displayed when hovering over the small image of the activity
		 */
		public String smallText;

		private Assets(final SjObject data) {
			largeImage = data.getString("large_image");
			largeText = data.getString("large_text");
			smallImage = data.getString("small_image");
			smallText = data.getString("small_text");
		}

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			if (largeImage != null)
				obj.put("large_image", largeImage);
			if (largeText != null)
				obj.put("large_text", largeText);
			if (smallImage != null)
				obj.put("small_image", smallImage);
			if (smallText != null)
				obj.put("small_text", smallText);
			return obj.toJsonString();
		}
	}

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-secrets
	 */
	public static class Secrets implements SjSerializable {
		/**
		 * Secret for joining a party
		 */
		public String join;

		/**
		 * Secret for spectating a game
		 */
		public String spectate;

		/**
		 * Secret for a specific instanced match
		 */
		public String match;

		private Secrets(final SjObject data) {
			join = data.getString("join");
			spectate = data.getString("spectate");
			match = data.getString("match");
		}

		@Override
		public String toJsonString() {
			final var obj = new SjObject();
			if (join != null)
				obj.put("join", join);
			if (spectate != null)
				obj.put("spectate", spectate);
			if (match != null)
				obj.put("match", match);
			return obj.toJsonString();
		}
	}

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-flags
	 */
	public static enum Flags implements BitFlag {
		INSTANCE,
		JOIN,
		SPECTATE,
		JOIN_REQUEST,
		SYNC,
		PLAY,
		PARTY_PRIVACY_FRIENDS,
		PARTY_PRIVACY_VOICE_CHANNEL,
		EMBEDDED;

		@Override
		public int getBitIndex() {
			return ordinal();
		}
	}

	/**
	 * https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-buttons
	 */
	public static class Button implements SjSerializable {
		/**
		 * Text shown on the button (1-32 characters). Required field.
		 */
		public String label;

		/**
		 * URL opened when clicking the button (1-512 characters).
		 * @apiNote Button URLs cannot be received from the gateway (in a {@link GatewayEvent#PRESENCE_UPDATE} event).
		 */
		public String url;

		public Button() {}

		public Button(final String label, final String url) {
			this.label = Objects.requireNonNull(label);
			this.url = Objects.requireNonNull(url);
		}

		private Button(final String data) {
			this.label = Objects.requireNonNull(data);
		}

		@Override
		public String toJsonString() {
			return "{\"label\":\"%s\",\"url\":\"%s\"}".formatted(label, url);
		}
	}

	/**
	 * Activity's name. Required when updating presence.
	 */
	public String name;

	/**
	 * Activity type. Required when updating presence.
	 */
	public Type type;

	/**
	 * Stream URL, is validated when type is {@link Type#STREAMING}. Can be {@code null}.
	 */
	public String url;

	/**
	 * When the activity was added to the user's session. Should not be sent when updating presence.
	 */
	public Instant createdAt;

	/**
	 * Timestamps for start and/or end of the game. Can be {@code null}.
	 */
	public Timestamps timestamps;

	/**
	 * Application ID for the game. Can be {@code null}.
	 */
	public String applicationId;

	/**
	 * What the player is currently doing. Can be {@code null}.
	 */
	public String details;

	/**
	 * User's current party status, or text used for a custom status. Can be {@code null}.
	 */
	public String state;

	/**
	 * Emoji used for a custom status. Can be {@code null}.
	 */
	public Emoji emoji;

	/**
	 * Information for the current party of the player. Can be {@code null}.
	 */
	public Party party;

	/**
	 * Images for the presence and their hover texts. Can be {@code null}.
	 */
	public Assets assets;

	/**
	 * Secrets for Rich Presence joining and spectating. Can be {@code null}.
	 */
	public Secrets secrets;

	/**
	 * Whether or not the activity is an instanced game session. Can be {@code null}.
	 */
	public Boolean instance;

	/**
	 * <a href="https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-flags">Activity flags</a>
	 * {@code OR}d together, describes what the payload includes. Can be {@code null}.
	 */
	public BitFlagSet<Flags> flags;

	/**
	 * Custom buttons shown in the Rich Presence (max 2). Can be {@code null}.
	 * @apiNote When received from the gateway (in a {@link GatewayEvent#PRESENCE_UPDATE} event),
	 *          button URLs are absent, probably for privacy reasons.
	 */
	public List<Button> buttons;

	/**
	 * @param data Data received from the {@link GatewayEvent#PRESENCE_UPDATE} event.
	 */
	public Activity(final SjObject data) {
		name = data.getString("name");
		type = Type.values()[data.getInteger("type")];
		url = data.getString("url");
		createdAt = Instant.ofEpochMilli(data.getInteger("created_at"));
		timestamps = data.containsKey("timestamps") ? new Timestamps(data.getObject("timestamps")) : null;
		applicationId = data.getString("application_id");
		details = data.getString("details");
		state = data.getString("state");
		emoji = data.containsKey("emoji") ? new Emoji(data.getObject("emoji")) : null;
		party = data.containsKey("party") ? new Party(data.getObject("party")) : null;
		assets = data.containsKey("assets") ? new Assets(data.getObject("assets")) : null;
		secrets = data.containsKey("secrets") ? new Secrets(data.getObject("secrets")) : null;
		instance = data.getBoolean("instance");
		flags = data.containsKey("flags") ? new MutableBitFlagSet<>(data.getLong("flags")) : null;
		buttons = data.containsKey("buttons") ? data.getStringArray("buttons").stream().filter(s -> s != null).map(Button::new).toList() : null;
	}

	/**
	 * Construct an empty {@link Activity} to be sent in a {@link GatewayOpcode#UPDATE_PRESENCE} event.
	 */
	public Activity() {}

	/**
	 * Construct a minimal {@link Activity} to be sent in a {@link GatewayOpcode#UPDATE_PRESENCE} event.
	 * @param name Activity's name. Cannot be {@code null}.
	 * @param type Activity type. Cannot be {@code null}.
	 */
	public Activity(final String name, final Type type) {
		this.name = Objects.requireNonNull(name);
		this.type = Objects.requireNonNull(type);
	}

	public SjObject toSjObject() {
		final var obj = new SjObject();
		obj.put("name", name);
		obj.put("type", type.ordinal());
		if (url != null)
			obj.put("url", url);
		if (timestamps != null)
			obj.put("timestamps", timestamps);
		if (details != null)
			obj.put("details", details);
		if (state != null)
			obj.put("state", state);
		if (emoji != null)
			obj.put("emoji", emoji);
		if (party != null)
			obj.put("party", party);
		if (assets != null)
			obj.put("assets", assets);
		if (secrets != null)
			obj.put("secrets", secrets);
		if (instance != null)
			obj.put("instance", instance);
		if (flags != null)
			obj.put("flags", flags.getBitset());
		return obj;
	}

	@Override
	public String toJsonString() {
		return toSjObject().toJsonString();
	}

	@Override
	public String toString() {
		return toSjObject().toPrettyJsonString();
	}
}
