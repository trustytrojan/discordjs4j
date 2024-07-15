package discord.structures;

import sj.SjObject;

public class ClientStatus {
	/**
	 * User's status set for an active desktop (Windows, Linux, Mac) application session
	 */
	public final String desktop;
	
	/**
	 * User's status set for an active mobile (iOS, Android) application session
	 */
	public final String mobile;
	
	/**
	 * User's status set for an active web (browser, bot user) application session
	 */
	public final String web;

	public ClientStatus(final SjObject data) {
		desktop = data.getString("desktop");
		mobile = data.getString("mobile");
		web = data.getString("web");
	}
}
