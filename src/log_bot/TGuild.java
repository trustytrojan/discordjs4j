package log_bot;

import org.json.simple.JSONObject;

import discord.util.BetterJSONObject;
import discord.util.JSONable;

public class TGuild implements JSONable {

	public static class LoggingOptions implements JSONable {

		public boolean enabled;
		public String channel;

		public LoggingOptions() {
			enabled = false;
			channel = null;
		}

		public LoggingOptions(BetterJSONObject data) {
			enabled = data.getBoolean("enabled");
			channel = data.getString("channel");
		}

		@Override
		public JSONObject toJSONObject() {
			final var obj = new BetterJSONObject();
			obj.put("enabled", enabled);
			obj.put("channel", channel);
			return obj.innerObject;
		}

	}

	public static class WelcomerOptions implements JSONable {

		public static class AutoMessageOptions implements JSONable {

			public String channel;
			public String message;

			public AutoMessageOptions() {
				channel = null;
				message = null;
			}

			public AutoMessageOptions(BetterJSONObject data) {
				channel = data.getString("channel");
				message = data.getString("message");
			}

			@Override
			public JSONObject toJSONObject() {
				final var obj = new BetterJSONObject();
				obj.put("channel", channel);
				obj.put("message", message);
				return obj.innerObject;
			}

		}

		public boolean enabled;
		public final AutoMessageOptions welcome;
		public final AutoMessageOptions goodbye;

		public WelcomerOptions() {
			enabled = false;
			welcome = new AutoMessageOptions();
			goodbye = new AutoMessageOptions();
		}

		public WelcomerOptions(BetterJSONObject data) {
			enabled = data.getBoolean("enabled");
			welcome = new AutoMessageOptions(data.getObject("welcome"));
			goodbye = new AutoMessageOptions(data.getObject("goodbye"));
		}

		@Override
		public JSONObject toJSONObject() {
			final var obj = new BetterJSONObject();
			obj.put("enabled", enabled);
			obj.put("welcome", welcome.toJSONObject());
			obj.put("goodbye", goodbye.toJSONObject());
			return obj.innerObject;
		}

	}

	public final String guild;
	public final LoggingOptions logging;
	public final WelcomerOptions welcomer;

	public TGuild(String guild_id) {
		guild = guild_id;
		logging = new LoggingOptions();
		welcomer = new WelcomerOptions();
	}

	public TGuild(BetterJSONObject data) {
		guild = data.getString("guild");
		logging = new LoggingOptions(data.getObject("logging"));
		welcomer = new WelcomerOptions(data.getObject("welcomer"));
	}

	@Override
	public JSONObject toJSONObject() {
		final var obj = new BetterJSONObject();
		obj.put("guild", guild);
		obj.put("logging", logging.toJSONObject());
		obj.put("welcomer", welcomer.toJSONObject());
		return obj.innerObject;
	}
	
}
